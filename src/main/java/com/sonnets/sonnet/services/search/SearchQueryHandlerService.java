package com.sonnets.sonnet.services.search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.annotation.Dialog;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Book;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.base.Section;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
    private final StandardAnalyzer standardAnalyzer;
    private final EntityManager entityManager;

    @Autowired
    public SearchQueryHandlerService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.standardAnalyzer = new StandardAnalyzer();
    }

    public JSONObject doSearch(SearchDto dto) {
        LOGGER.debug("Parsing dto into search: " + dto.toString());
        Gson gson = new Gson();
        Gson sectionGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        try {
            FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
            JSONObject results = new JSONObject();
            if (dto.isSearchBookCharacters()) {
                Query query = CharacterHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, BookCharacter.class);
                results.put(TypeConstants.BOOK_CHARACTER, new JSONArray(gson.toJson(fullTextQuery.getResultList())));
            }
            if (dto.isSearchDialog()) {
                Query query = DialogHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Dialog.class);
                results.put(TypeConstants.DIALOG, new JSONArray(gson.toJson(fullTextQuery.getResultList())));
            }
            if (dto.isSearchPoems()) {
                Query query = PoemHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Poem.class);
                results.put(TypeConstants.POEM, new JSONArray(gson.toJson(fullTextQuery.getResultList())));
            }
            if (dto.isSearchBooks()) { // :todo: fix this when you give a shit.
                Query query = SectionHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Section.class);
                results.put(TypeConstants.SECTION, new JSONArray(
                        sectionGson.toJson(fullTextQuery.getResultList())));
            }

            LOGGER.debug("Found: " + results.length());
            return results;
        } catch (JSONException e) {
            LOGGER.error(e);
            return null;
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
        LOGGER.debug("Searching for author: " + dto.toString());
        String queryString = "firstName: \"" + dto.getFirstName() + "\" AND lastName: \"" + dto.getLastName() + "\"";
        Query q = new QueryParser(null, standardAnalyzer).parse(queryString);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(q, Author.class);
        return fullTextQuery.getResultList();
    }

    /**
     * Searches for matched poems with the same author first/last name and title.
     *
     * @param dto a SearchDto containing the information to search for.
     * @throws ParseException if the query doesn't parse.
     */
    public void similarExistsPoem(SearchDto dto) throws ParseException {
        LOGGER.debug("Searching form poems similar to: " + dto.toString());
        String queryString =
                "author.firstName: \"" + dto.getAuthor().getFirstName() +
                        "\" AND author.lastName: \"" + dto.getAuthor().getLastName() +
                        "\" AND title: \"" + dto.getTitle() + "\"";
        Query q = new QueryParser(null, standardAnalyzer).parse(queryString);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(q, Author.class);
        if (fullTextQuery.getResultSize() > 0) {
            LOGGER.error("Found a similar poem!!");
            throw new ItemAlreadyExistsException("Item: '" + dto.toString() + "' already exists.");
        }
    }

    public List findBookByTitle(final String title) {
        LOGGER.debug("Searching for books similar to: " + title);
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        int counter = 0;
        for (String s : title.split(" ")) {
            builder.add(new Term("title", s), counter);
            counter++;
        }
        PhraseQuery query = builder.build();
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Book.class);
        return fullTextQuery.getResultList();
    }
}
