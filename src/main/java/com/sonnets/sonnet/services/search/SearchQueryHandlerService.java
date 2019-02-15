package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Book;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.base.Section;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Parses a Lucene query string and returns the results.
     *
     * @param queryString the query string to parse.
     * @return a list of results.
     */
    public List doSearch(String queryString) throws ParseException {
        LOGGER.debug("Parsing query string: " + queryString);
        Query q = new QueryParser(null, standardAnalyzer).parse(queryString);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(q, Poem.class, Book.class, Section.class);
        return fullTextQuery.getResultList();
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
}
