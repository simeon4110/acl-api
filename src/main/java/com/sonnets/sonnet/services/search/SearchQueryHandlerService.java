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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
     * Parse a List of SearchParams into a valid Lucene query string.
     *
     * @param dtos the list of Params to parse.
     * @return a String formatted into a Lucene query string.
     */
    private static String parseSearchParams(List<SearchParamDto> dtos) {
        StringBuilder sb = new StringBuilder();

        // Handle row one and remove it from the list.
        sb.append(String.format("(%s: '%s') ", dtos.get(0).getFieldName(), dtos.get(0).getSearchString()));
        dtos.remove(0);

        // Parse remaining params.
        if (!dtos.isEmpty()) {
            for (SearchParamDto d : dtos) {
                sb.append(d.getJoinType());
                sb.append(String.format(" (%s: '%s') ", d.getFieldName(), d.getSearchString()));
            }
        }

        return sb.toString().trim();
    }

    /**
     * Grabs the most relevant fragment from a completed search result / query.
     *
     * @param query    an executed query.
     * @param topDocs  the TopDocs result.
     * @param searcher the IndexSearcher used to execute the query.
     * @return a JSONArray with the relevant fragment.
     */
    private List<Map<String, String>> highlightResults(final Query query, final TopDocs topDocs, final IndexSearcher searcher) {
        Formatter formatter = new SimpleHTMLFormatter();
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, SearchConstants.FRAGMENT_SIZE);
        highlighter.setTextFragmenter(fragmenter);

        // :todo: fix the multiple duplicated docs showing up in results.
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
                objectOut.put(SearchConstants.BEST_FRAGMENT,
                        highlighter.getBestFragment(this.analyzer, SearchConstants.TEXT,
                                document.getField(SearchConstants.TEXT).stringValue()));

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
                // Parse params into a query string.
                String queryString = parseSearchParams(params);
                LOGGER.debug(String.format("[SEARCH] :::::: Query string: \"%s\"", queryString));

                // Init a searcher and analyzer, and parse the query string into a Query.
                IndexSearcher searcher = new IndexSearcher(reader);
                Query query = new QueryParser(null, analyzer).parse(queryString);

                // Execute search and append results to out JSONArray.
                TopDocs hits = searcher.search(query, SearchConstants.MAX_RESULT_SIZE);
                out.addAll(highlightResults(query, hits, searcher));
            } catch (IOException e) {
                LOGGER.error(String.format("[SEARCH] :::::: Error opening %s index", i));
                return gson.toJson(e.getMessage());
            } catch (ParseException e) {
                LOGGER.error("[SEARCH] :::::: Error parsing query / json");
                return gson.toJson(e.getMessage());
            }
        }

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
