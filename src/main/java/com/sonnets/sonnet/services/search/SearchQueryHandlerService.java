package com.sonnets.sonnet.services.search;

import com.google.gson.Gson;
import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.dtos.web.SearchParamDto;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
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
    private final Analyzer analyzer = new StandardAnalyzer();

    private static IndexReader getReader(final String itemType) throws IOException {
        return DirectoryReader.open(FSDirectory.open(
                Paths.get(String.format("%s/%s", SearchConstants.DOCS_PATH, itemType))
        ));
    }

    /**
     * @param in    the search string.
     * @param field the field to search.
     * @return a TermQuery or PhraseQuery depending on the logic below.
     */
    private static Query parseField(final String in, final String field) {
        if (in.split(" ").length > 1) {
            // Titles and Authors cannot be handled like phrases.
            if (field.equals(SearchConstants.PARENT_TITLE) || field.equals(SearchConstants.AUTHOR_FIRST_NAME) ||
                    field.equals(SearchConstants.AUTHOR_LAST_NAME)) {
                return new TermQuery(new Term(field, in));
            }

            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            for (String s : in.split(" ")) {
                builder.add(new Term(field, s));
            }
            builder.setSlop(SearchConstants.SLOP);
            return builder.build();
        } else {
            return new TermQuery(new Term(field, in));
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
            builder.add(parseField(dtos.get(0).getSearchString(), dtos.get(0).getFieldName()),
                    BooleanClause.Occur.MUST);
            dtos.remove(0);
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
     * Grabs the most relevant fragment from a completed search result / query.
     *
     * @param query    an executed query.
     * @param topDocs  the TopDocs result.
     * @param searcher the IndexSearcher used to execute the query.
     * @return a JSONArray with the relevant fragment.
     */
    private List<Map<String, String>> highlightResults(final Query query, final TopDocs topDocs,
                                                       final IndexSearcher searcher) {
        Formatter formatter = new SimpleHTMLFormatter();
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
                objectOut.put(SearchConstants.TEXT, document.get(SearchConstants.TEXT));
                objectOut.put(SearchConstants.PARENT_TITLE, document.get(SearchConstants.PARENT_TITLE));

                // Text highlighting; returns up to MAX_FRAGMENTS matching passages.
                objectOut.put(SearchConstants.BEST_FRAGMENT,
                        String.join(" ::: ", highlighter.getBestFragments(this.analyzer, SearchConstants.TEXT,
                                document.get(SearchConstants.TEXT), SearchConstants.MAX_FRAGMENTS))
                                .replace("\n", " ").trim());
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
            try (IndexReader reader = getReader(i)) {
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
     * Search for an exact match on an author by first and last name.
     *
     * @param dto an AuthorDto with the first and last name.
     * @return the list of results (an empty array if there are not results.)
     * @throws ParseException if the query doesn't parse.
     */
    public List searchAuthor(AuthorDto dto) throws ParseException {
        return null;
    }

    /**
     * Searches for matched poems with the same author first/last name and title.
     *
     * @param dto a SearchDto containing the information to search for.
     * @throws ParseException if the query doesn't parse.
     */
    public void similarExistsPoem(SearchDto dto) throws ParseException {
        LOGGER.debug("Searching form poems similar to: " + dto.toString());
    }
}
