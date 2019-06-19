package org.acl.database.services.search;

import com.google.gson.Gson;
import org.acl.database.config.LuceneConfig;
import org.acl.database.persistence.dtos.base.AuthorDto;
import org.acl.database.persistence.dtos.web.SearchParamDto;
import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.search.SearchRepository;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Routes a SearchDto into queries based on boolean flags set in the dto.
 *
 * @author Josh Harkema
 */
@Service
public class SearchQueryHandlerService {
    private static final Logger LOGGER = Logger.getLogger(SearchQueryHandlerService.class);
    private static final Gson gson = new Gson();
    private static final String[] itemTypes = new String[]{
            TypeConstants.POEM,
            TypeConstants.SECTION,
            TypeConstants.PLAY,
            TypeConstants.DILI
    };

    /**
     * @param in    the search string.
     * @param field the field to search.
     * @return a TermQuery or PhraseQuery depending on the logic below.
     */
    private static Query parseField(String in, final String field) {
        in = in.toLowerCase();
        if (in.split(" ").length > 1) {
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            for (String s : in.split(" ")) {
                builder.add(new Term(field, s.trim()));
            }
            builder.setSlop(SearchConstants.SLOP);
            return builder.build();
        } else {
            return new TermQuery(new Term(field, in.trim()));
        }
    }

    /**
     * Parse a List of SearchParams into a valid Lucene query string.
     *
     * @param dtos the list of Params to parse.
     * @return a String formatted into a Lucene query string.
     */
    private static Query parseSearchParams(final List<SearchParamDto> dtos) {
        if (dtos.size() == 1) {
            return parseField(dtos.get(0).getSearchString(), dtos.get(0).getFieldName());
        } else {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (SearchParamDto d : dtos) {
                BooleanClause.Occur clause;
                switch (d.getJoinType()) {
                    case "OR":
                        clause = BooleanClause.Occur.SHOULD;
                        break;
                    case "NOT":
                        clause = BooleanClause.Occur.MUST_NOT;
                        break;
                    default:
                        clause = BooleanClause.Occur.MUST;
                        break;
                }
                builder.add(parseField(d.getSearchString(), d.getFieldName()), clause);
            }
            return builder.build();
        }
    }

    /**
     * Tests to see if a poem with by an author has the same title.
     *
     * @param title    the title of the poem.
     * @param lastName the last name of the author.
     * @return true if a similar poem exists, false otherwise (including error.)
     */
    public static boolean similarPoemExists(final String title, final String lastName) {
        LOGGER.debug(String.format("[SEARCH] :::::: Searching for poem with title '%s' by '%s'.", title,
                lastName));
        try (IndexReader reader = SearchRepository.getReader(TypeConstants.POEM)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(parseField(title, SearchConstants.TITLE), BooleanClause.Occur.MUST);
            builder.add(new TermQuery(new Term(SearchConstants.AUTHOR_LAST_NAME, lastName)), BooleanClause.Occur.MUST);
            TopDocs hits = searcher.search(builder.build(), 1, Sort.RELEVANCE);
            return hits.totalHits.value == 0;
        } catch (IOException e) {
            LOGGER.error(String.format("[SEARCH] :::::: Error opening \"%s\" index.", TypeConstants.POEM));
            return false;
        }
    }

    /**
     * Grabs the most relevant fragment from a completed search result / query.
     *
     * @param query    an executed query.
     * @param topDocs  the TopDocs result.
     * @param searcher the IndexSearcher used to execute the query.
     * @return a JSONArray with the relevant fragment.
     */
    private List<Map<String, String>> highlightResults(final Query query, final TopDocs topDocs,
                                                       final IndexSearcher searcher) {
        Formatter formatter = new SimpleHTMLFormatter("<span class='highlight'>", "</span>");
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, SearchConstants.FRAGMENT_SIZE);
        highlighter.setTextFragmenter(fragmenter);

        List<Map<String, String>> out = new ArrayList<>();
        for (ScoreDoc d : topDocs.scoreDocs) {
            try {
                Document document = searcher.doc(d.doc);
                Map<String, String> objectOut = new HashMap<>();

                objectOut.put(SearchConstants.AUTHOR_FIRST_NAME, document.get(SearchConstants.AUTHOR_FIRST_NAME));
                objectOut.put(SearchConstants.AUTHOR_LAST_NAME, document.get(SearchConstants.AUTHOR_LAST_NAME));
                objectOut.put(SearchConstants.ID, document.getField(SearchConstants.ID).stringValue());
                objectOut.put(SearchConstants.TITLE, document.get(SearchConstants.TITLE));
                objectOut.put(SearchConstants.CATEGORY, document.get(SearchConstants.CATEGORY));
                objectOut.put(SearchConstants.PERIOD, document.get(SearchConstants.PERIOD));
                objectOut.put(SearchConstants.IS_PUBLIC, document.get(SearchConstants.IS_PUBLIC));
                objectOut.put(SearchConstants.TOPIC_MODEL, document.get(SearchConstants.TOPIC_MODEL));
                objectOut.put(SearchConstants.PARENT_TITLE, document.get(SearchConstants.PARENT_TITLE));

                // Text highlighting; returns up to MAX_FRAGMENTS matching passages.
                objectOut.put(SearchConstants.BEST_FRAGMENT,
                        String.join(SearchConstants.FRAGMENT_DELIMITER, highlighter.getBestFragments(
                                LuceneConfig.getAnalyzer(),
                                SearchConstants.TEXT,
                                document.get(SearchConstants.TEXT),
                                SearchConstants.MAX_FRAGMENTS)));
                out.add(objectOut);
            } catch (IOException e) {
                LOGGER.error(e);
                LOGGER.error(String.format("[SEARCH] :::::: Error processing document '%s'", d.doc));
            } catch (InvalidTokenOffsetsException e) {
                LOGGER.error(e);
            }
        }
        return out;
    }

    /**
     * Execute a search.
     *
     * @param params    the list of search parameters to use.
     * @param itemTypes the list of item types to search for.
     * @return a JSON formatted string of the results.
     */
    public String search(final List<SearchParamDto> params, final String[] itemTypes) {
        List<Map<String, String>> out = new ArrayList<>();
        for (String i : itemTypes) {
            i = i.toUpperCase(); // Types are always uppercase.
            try (IndexReader reader = SearchRepository.getReader(i)) {
                // Init a searcher and analyzer, and parse the query string into a Query.
                IndexSearcher searcher = new IndexSearcher(reader);
                Query query = parseSearchParams(params);
                LOGGER.debug(String.format("[SEARCH] :::::: Query string: \"%s\"", query));

                // Execute search and append results to out JSONArray.
                TopFieldDocs hits = searcher.search(query, SearchConstants.MAX_RESULT_SIZE, Sort.RELEVANCE);
                out.addAll(highlightResults(query, hits, searcher));
            } catch (IOException e) {
                LOGGER.error(String.format("[SEARCH] :::::: Error opening \"%s\" index.", i));
                Map<String, String> errorOut = new HashMap<>();
                errorOut.put("error", "Something went wrong with the search indexes; it's not you, it's me.");
                return gson.toJson(errorOut);
            }
        }
        LOGGER.debug("[SEARCH] :::::: Total results: " + out.size());
        return gson.toJson(out);
    }

    /**
     * Runs a basic search on all object types.
     *
     * @param searchString the search string (not a query, just the string to search for.)
     * @return the search results if any.
     */
    public String basicSearch(final String searchString) {
        LOGGER.debug("[SEARCH] :::::: executing basic search: " + searchString);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(
                new String[]{SearchConstants.TEXT, SearchConstants.AUTHOR_FIRST_NAME,
                        SearchConstants.AUTHOR_LAST_NAME, SearchConstants.TITLE},
                LuceneConfig.getAnalyzer()
        );
        List<Map<String, String>> out = new ArrayList<>();
        for (String s : itemTypes) {
            try (IndexReader reader = SearchRepository.getReader(s)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                Query query = parser.parse(searchString);
                TopDocs hits = searcher.search(query, SearchConstants.MAX_RESULT_SIZE, Sort.RELEVANCE);
                out.addAll(highlightResults(query, hits, searcher));
            } catch (ParseException | IOException e) {
                LOGGER.error(String.format("[SEARCH] :::::: Error opening \"%s\" index.", s));
                Map<String, String> errorOut = new HashMap<>();
                errorOut.put("error", "Something went wrong with the search indexes; it's not you, it's me.");
                return gson.toJson(errorOut);
            }
        }
        LOGGER.debug("[SEARCH] :::::: Total results: " + out.size());
        return gson.toJson(out);
    }

    /**
     * Search for an exact match on an author by first and last name.
     *
     * @param dto an AuthorDto with the first and last name.
     * @return the list of results (an empty array if there are not results.)
     */
    public String searchAuthor(AuthorDto dto) {
        LOGGER.debug("[SEARCH] :::::: Searching for author: " + dto.toString());
        try (IndexReader reader = SearchRepository.getReader(TypeConstants.AUTHOR)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();

            if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
                builder.add(parseField(dto.getFirstName(), SearchConstants.AUTHOR_FIRST_NAME),
                        BooleanClause.Occur.MUST);
            }

            if (dto.getFirstName() != null && !dto.getLastName().isEmpty()) {
                builder.add(parseField(dto.getLastName(), SearchConstants.AUTHOR_LAST_NAME), BooleanClause.Occur.MUST);
            }

            TopDocs hits = searcher.search(builder.build(), SearchConstants.MAX_RESULT_SIZE, Sort.RELEVANCE);
            LOGGER.debug(String.format("[SEARCH] :::::: found %s results!", hits.totalHits.value));
            List<Map<String, String>> out = new ArrayList<>();
            for (ScoreDoc d : hits.scoreDocs) {
                Document document = searcher.doc(d.doc);
                Map<String, String> outMap = new HashMap<>();
                outMap.put(SearchConstants.ID, document.get(SearchConstants.ID));
                outMap.put(SearchConstants.AUTHOR_FIRST_NAME, document.get(SearchConstants.AUTHOR_FIRST_NAME));
                outMap.put(SearchConstants.AUTHOR_LAST_NAME, document.get(SearchConstants.AUTHOR_LAST_NAME));
                out.add(outMap);
            }
            return gson.toJson(out);
        } catch (IOException e) {
            LOGGER.error(String.format("[SEARCH] :::::: Error opening \"%s\" index.", TypeConstants.AUTHOR));
            LOGGER.error(e);
            Map<String, String> errorOut = new HashMap<>();
            errorOut.put("error", "Something went wrong with the search indexes; it's not you, it's me.");
            return gson.toJson(errorOut);
        }
    }
}
