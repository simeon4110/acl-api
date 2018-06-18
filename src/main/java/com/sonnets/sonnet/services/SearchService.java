package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.exceptions.SonnetAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.Sonnet;
import org.apache.log4j.Logger;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.toIntExact;

/**
 * Field specific searching. Can only query one field at a time.
 *
 * @author Josh Harkema
 */
@Repository
public class SearchService {
    private EntityManager entityManager;
    private static final Logger LOGGER = Logger.getLogger(SearchService.class);
    private static final int PREFIX_LENGTH = 2;
    private static final int EDIT_DISTANCE = 2;
    private static final int YEAR_RANGE = 10;

    @Autowired
    public SearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Dynamically generates a query based on which field is selected.
     *
     * @param sonnet       the sonnet object with the query params.
     * @param queryBuilder the query builder to build the query for.
     * @return a lucene query.
     */
    private static org.apache.lucene.search.Query evalFields(Sonnet sonnet, QueryBuilder queryBuilder) {
        LOGGER.debug("Searching for sonnet: " + sonnet.toString());
        org.apache.lucene.search.Query query = null;

        // firstName
        if (sonnet.getFirstName() != null && !Objects.equals(sonnet.getFirstName(), "")) {
            query = queryBuilder.keyword().fuzzy().withPrefixLength(PREFIX_LENGTH).withEditDistanceUpTo(EDIT_DISTANCE)
                    .onField("firstName").matching(sonnet.getFirstName()).createQuery();
        }

        // lastName
        if (sonnet.getLastName() != null && !Objects.equals(sonnet.getLastName(), "")) {
            query = queryBuilder.keyword().fuzzy().withPrefixLength(PREFIX_LENGTH).withEditDistanceUpTo(EDIT_DISTANCE)
                    .onField("lastName").matching(sonnet.getLastName()).createQuery();
        }

        // title
        if (sonnet.getTitle() != null && !Objects.equals(sonnet.getTitle(), "")) {
            query = queryBuilder.keyword().fuzzy().withPrefixLength(0).withEditDistanceUpTo(1).onField("title")
                    .matching(sonnet.getTitle()).createQuery();
        }

        // Period
        if (sonnet.getPeriod() != null && !Objects.equals(sonnet.getPeriod(), "")) {
            query = queryBuilder.keyword().onField("period").matching(sonnet.getPeriod()).createQuery();
        }

        // Publication Year
        if (sonnet.getPublicationYear() != null) {
            query = queryBuilder.range().onField("publicationYear").from(sonnet.getPublicationYear() - YEAR_RANGE)
                    .to(sonnet.getPublicationYear() + YEAR_RANGE).createQuery();
        }

        // text
        if (sonnet.getText() != null && !sonnet.getText().isEmpty()) {
            query = queryBuilder.phrase().onField("text").sentence(sonnet.getText().toString()).createQuery();
        }

        // default
        if (query == null) {
            query = queryBuilder.all().createQuery();
        }

        return query;
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
                .must(queryBuilder.phrase().onField("title").sentence(sonnet.getTitle()).createQuery())
                .must(queryBuilder.keyword().onField("lastName").matching(sonnet.getLastName()).createQuery())
                .must(queryBuilder.phrase().onField("source").sentence(sonnet.getSourceDesc()).createQuery())
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
     * Searches the db based on user params from front end.
     *
     * @param sonnet      the sonnet holding the params.
     * @param pageRequest the pagination object for paging results.
     * @return a paged list of search results.
     */
    public PageImpl<Sonnet> search(Sonnet sonnet, Pageable pageRequest) {
        LOGGER.debug("Searching for: " + sonnet.toString());

        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();

        Query fullTextQuery = manager.createFullTextQuery(evalFields(sonnet, queryBuilder), Sonnet.class);
        long total = fullTextQuery.getResultList().size();

        fullTextQuery.setFirstResult(toIntExact(pageRequest.getOffset())).setMaxResults(pageRequest.getPageSize());

        List<Sonnet> results;

        try {
            //noinspection unchecked
            results = fullTextQuery.getResultList();
            LOGGER.debug("Found matching sonnets: " + total);
        } catch (NoResultException e) {
            LOGGER.error(e);
            return null;
        }

        return new PageImpl<>(results, pageRequest, total);
    }

}
