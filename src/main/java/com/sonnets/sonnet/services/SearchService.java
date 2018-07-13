package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.exceptions.SonnetAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.Sonnet;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Handles dynamic BooleanQuery building and other Lucene focused search jobs.
 *
 * @author Josh Harkema
 */
@Repository
public class SearchService {
    private final EntityManager entityManager;
    private static final Logger LOGGER = Logger.getLogger(SearchService.class);

    // Lucene fuzzy and phrase query constants.
    private static final int PREFIX_LENGTH = 0; // How many chars are "fixed" to the front.
    private static final int EDIT_DISTANCE = 2; // Levenstein edit distance.
    private static final int MAX_EXPANSIONS = 2; // How many words must match.
    private static final int SLOP = 1; // Words are directly adjacent.

    // Field name constants:
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String TITLE = "title";
    private static final String PERIOD = "period";
    private static final String TEXT = "text";
    private static final String SOURCE = "source";


    @Autowired
    public SearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Dynamic boolean search automatically parsed from the non-null searchDto fields.
     *
     * @param firstName the Author's first name.
     * @param lastName  the Author's last name.
     * @param title     the title of the sonnet.
     * @param period    the period of the sonnet's composition.
     * @param text      some text to search the sonnet's body for.
     * @return a list of results.
     */
    public Page<Sonnet> executeSearch(final String firstName, final String lastName, final String title,
                                      final String period, final String text, final Pageable pageable) {
        LOGGER.debug("Parsing search: " + firstName + " " + lastName + " " + title + " " + period + " " + text);

        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        BooleanQuery.Builder booleanClauses = new BooleanQuery.Builder();

        // Add first name.
        if (!Objects.equals(firstName, "") && firstName != null) {
            LOGGER.debug(FIRST_NAME);
            TermQuery tq = new TermQuery(new Term(FIRST_NAME, firstName));
            booleanClauses.add(tq, BooleanClause.Occur.MUST);
        }

        // Add last name.
        if (!Objects.equals(lastName, "") && lastName != null) {
            LOGGER.debug(LAST_NAME);
            FuzzyQuery fq = new FuzzyQuery(
                    new Term(LAST_NAME, lastName),
                    EDIT_DISTANCE, PREFIX_LENGTH, MAX_EXPANSIONS, true);
            booleanClauses.add(fq, BooleanClause.Occur.MUST);
        }

        // Add title.
        if (!Objects.equals(title, "") && title != null) {
            LOGGER.debug(TITLE);
            FuzzyQuery fq = new FuzzyQuery(
                    new Term(TITLE, title), EDIT_DISTANCE, PREFIX_LENGTH);
            booleanClauses.add(fq, BooleanClause.Occur.MUST);
        }

        // Add period.
        if (!Objects.equals(period, "") && period != null) {
            LOGGER.debug(PERIOD);
            TermQuery tq = new TermQuery(new Term(PERIOD, period));
            booleanClauses.add(tq, BooleanClause.Occur.MUST);
        }

        // Add text search.
        if (!Objects.equals(text, "") && text != null) {
            LOGGER.debug(TEXT);
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            builder.setSlop(SLOP);
            int counter = 0;
            for (String term : text.split(" ")) {
                builder.add(new Term(TEXT, term), counter);
                counter++;
            }
            booleanClauses.add(builder.build(), BooleanClause.Occur.MUST);
        }

        // Build and execute query.
        org.apache.lucene.search.Query query = booleanClauses.build();

        Query fullTextQuery = manager.createFullTextQuery(query, Sonnet.class);

        // Manually set pageable offset. Getting this to work was a bloody nightmare.
        fullTextQuery.setFirstResult((int) pageable.getOffset());
        fullTextQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Sonnet> results = fullTextQuery.getResultList();

        LOGGER.debug("Found total records: " + ((FullTextQuery) fullTextQuery).getResultSize());
        return new PageImpl<>(results, pageable, ((FullTextQuery) fullTextQuery).getResultSize());
    }

    /**
     * This checks to see if a sonnet with similar data already exists in the database.
     *
     * @param sonnet the sonnet to check for duplicates.
     */
    void similarExists(SonnetDto sonnet) {
        LOGGER.debug("Similar Search: " + sonnet.toString());
        org.apache.lucene.search.Query query;
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();

        // Use first line as title if title is null.
        if (Objects.equals(sonnet.getTitle(), "")) {
            sonnet.setTitle(Sonnet.parseText(sonnet.getText().split("\\r?\\n")).get(0));
        }

        // Boolean "must" query searches for the exact title, lastname and source of a sonnet.
        query = queryBuilder.bool()
                .must(queryBuilder.phrase().onField(TITLE).sentence(sonnet.getTitle()).createQuery())
                .must(queryBuilder.keyword().onField(LAST_NAME).matching(sonnet.getLastName()).createQuery())
                .must(queryBuilder.phrase().onField(SOURCE).sentence(sonnet.getSourceDesc()).createQuery())
                .createQuery();

        Query fullTextQuery = manager.createFullTextQuery(query, Sonnet.class);

        List results = fullTextQuery.getResultList();

        LOGGER.debug("Found similar: " + results.toString());

        // If the search turned up any results, throw a SonnetAlreadyExists exception.
        if (!results.isEmpty()) {
            throw new SonnetAlreadyExistsException("Sonnet by: " + sonnet.getLastName()
                    + "With title: " + sonnet.getTitle()
                    + " Already exists.");
        }

    }

    /**
     * Search the Sonnet table's "text" column for an arbitrary string.
     *
     * @param text the string to search for.
     * @return the list of results or null.
     */
    public List searchByText(String text) {
        LOGGER.debug("Searching for sonnets with text: " + text);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();
        org.apache.lucene.search.Query query = queryBuilder.phrase().onField(TEXT).sentence(text).createQuery();

        return executeQuery(query, manager);
    }

    /**
     * Search the Sonnet table's "title" column for an arbitrary string (fuzzy)
     *
     * @param title the keywords to look for.
     * @return a list of results or null.
     */
    public List searchByTitle(String title) {
        LOGGER.debug("Searching for sonnets with title: " + title);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();
        org.apache.lucene.search.Query query = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(EDIT_DISTANCE)
                .withPrefixLength(EDIT_DISTANCE).onField(TITLE).matching(title).createQuery();

        return executeQuery(query, manager);
    }

    /**
     * Search the Sonnet table's "period" column for a period.
     *
     * @param period the period to search for.
     * @return a list of results or null.
     */
    public List searchByPeriod(String period) {
        LOGGER.debug("Searching for sonnets by period: " + period);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();
        org.apache.lucene.search.Query query = queryBuilder.keyword().onField(PERIOD).matching(period)
                .createQuery();

        return executeQuery(query, manager);
    }

    /**
     * Executor to run pre-parsed lucene queries.
     *
     * @param query the query to execute.
     * @return the query results as a list of Sonnets.
     */
    private List executeQuery(org.apache.lucene.search.Query query, FullTextEntityManager manager) {
        Query fullTextQuery = manager.createFullTextQuery(query, Sonnet.class);
        long total = fullTextQuery.getResultList().size();

        List results;

        try {
            results = fullTextQuery.getResultList();
            LOGGER.debug("Found matching sonnets: " + total);
            return results;
        } catch (NoResultException e) {
            LOGGER.error(e);
            return Collections.emptyList();
        }

    }

}
