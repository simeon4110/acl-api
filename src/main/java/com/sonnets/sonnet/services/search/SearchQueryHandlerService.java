package com.sonnets.sonnet.services.search;

import com.google.gson.Gson;
import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Routes a SearchDto into queries based on boolean flags set in the dto.
 *
 * @author Josh Harkema
 */
@Service
@Transactional
public class SearchQueryHandlerService {
    private static final Logger LOGGER = Logger.getLogger(SearchQueryHandlerService.class);
    private static final StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
    private static final Gson gson = new Gson();

    private final EntityManager entityManager;

    @Autowired
    public SearchQueryHandlerService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private static Query getQuery(SearchParam param) {
        if (param.getSearchString().split(" ").length == 1) {
            return new TermQuery(new Term(param.getFieldName(), param.getSearchString()));
        }
        return new QueryBuilder(standardAnalyzer).createPhraseQuery(param.getFieldName(),
                param.getSearchString());
    }

    private static String highlightText(Query query, Analyzer analyzer, String fieldName, String text) {
        QueryScorer queryScorer = new QueryScorer(query);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span>", "</span>");
        Highlighter highlighter = new Highlighter(formatter, queryScorer);
        try {
            return highlighter.getBestFragment(analyzer, fieldName, text);
        } catch (IOException | InvalidTokenOffsetsException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    private static List<String> parseClasses(final String[] toParse) {
        if (toParse == null || toParse.length == 0) {
            return Arrays.asList(
                    "Book",
                    "Poem",
                    "Section",
                    "ShortStory"
            );
        } else { // Otherwise, parse the list into a list of classes and use the list to build a fullTextQuery.
            ArrayList<String> parsedClasses = new ArrayList<>();
            for (String s : toParse) {
                switch (s.toLowerCase()) {
                    case "book":
                        parsedClasses.add("Book");
                        break;
                    case "poem":
                        parsedClasses.add("Poem");
                        break;
                    case "section":
                        parsedClasses.add("Section");
                        break;
                    case "short story":
                        parsedClasses.add("ShortStory");
                        break;
                    case "any":
                        parsedClasses.add("Poem");
                        parsedClasses.add("Section");
                        parsedClasses.add("ShortStory");
                        break;
                }
            }
            return parsedClasses;
        }
    }

    private String executeSearch(Query query, String[] itemTypes) throws JSONException {
        return null;
    }

    public String parseSearch(List<SearchParam> params, String[] itemTypes) {
        LOGGER.debug("Parsing query string: " + params + " on object types " + Arrays.toString(itemTypes));
        try {
            // Catch single field queries.
            if (params.size() == 1) {
                return executeSearch(getQuery(params.get(0)), itemTypes);
            }

            // Deal with multi-field query.
            final BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (SearchParam param : params) {
                BooleanClause.Occur clause;
                switch (param.getJoinType()) {
                    case "OR":
                        clause = BooleanClause.Occur.SHOULD;
                        break;
                    case "NOT":
                        clause = BooleanClause.Occur.MUST_NOT;
                        break;
                    default:
                        clause = BooleanClause.Occur.MUST;
                }
                builder.add(getQuery(param), clause);
            }

            return executeSearch(builder.build(), itemTypes);

        } catch (JSONException e) {
            LOGGER.error(e);
            return gson.toJson(e);
        }
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
