package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.exceptions.SonnetAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.Sonnet;
import org.apache.log4j.Logger;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
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
@Transactional
public class SearchService {
    private static final Logger logger = Logger.getLogger(SearchService.class);
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Dynamically generates a query based on which field is selected.
     *
     * @param sonnet       the sonnet object with the query params.
     * @param queryBuilder the query builder to build the query for.
     * @return a lucene query.
     */
    private static org.apache.lucene.search.Query evalFields(Sonnet sonnet, QueryBuilder queryBuilder) {
        org.apache.lucene.search.Query query = null;

        // firstName
        if (sonnet.getFirstName() != null && !Objects.equals(sonnet.getFirstName(), "")) {
            logger.debug(sonnet.getFirstName());
            query = queryBuilder.keyword().fuzzy().withPrefixLength(2).withEditDistanceUpTo(2).onField("firstName")
                    .matching(sonnet.getFirstName()).createQuery();
        }

        // lastName
        if (sonnet.getLastName() != null && !Objects.equals(sonnet.getLastName(), "")) {
            logger.debug(sonnet.getLastName());
            query = queryBuilder.keyword().fuzzy().withPrefixLength(2).withEditDistanceUpTo(2).onField("lastName")
                    .matching(sonnet.getLastName()).createQuery();
        }

        // title
        if (sonnet.getTitle() != null && !Objects.equals(sonnet.getTitle(), "")) {
            logger.debug(sonnet.getTitle());
            query = queryBuilder.keyword().fuzzy().withPrefixLength(0).withEditDistanceUpTo(1).onField("title")
                    .matching(sonnet.getTitle()).createQuery();
        }

        // Period
        if (sonnet.getPeriod() != null && !Objects.equals(sonnet.getPeriod(), "")) {
            logger.debug(sonnet.getPeriod());
            query = queryBuilder.keyword().onField("period").matching(sonnet.getPeriod()).createQuery();
        }

        // Publication Year
        if (sonnet.getPublicationYear() != null) {
            logger.debug(sonnet.getPublicationYear());
            query = queryBuilder.range().onField("publicationYear").above(sonnet.getPublicationYear() + 10)
                    .createQuery();
        }

        // text
        if (sonnet.getText() != null && !sonnet.getText().isEmpty()) {
            logger.debug(sonnet.getText());
            query = queryBuilder.phrase().onField("text").sentence(sonnet.getText().toString()).createQuery();
        }

        // default
        if (query == null) {
            query = queryBuilder.all().createQuery();
        }

        return query;
    }

    public boolean similarExists(SonnetDto sonnet) {
        org.apache.lucene.search.Query query;
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();

        if (Objects.equals(sonnet.getTitle(), "")) {
            sonnet.setTitle(Sonnet.parseText(sonnet.getText().split("\\r?\\n")).get(0));
        }

        query = queryBuilder.bool()
                .must(queryBuilder.keyword().onField("title").matching(sonnet.getTitle()).createQuery())
                .must(queryBuilder.keyword().onField("lastName").matching(sonnet.getLastName()).createQuery())
                .createQuery();

        Query fullTextQuery = manager.createFullTextQuery(query, Sonnet.class);

        List results = fullTextQuery.getResultList();

        logger.debug(results.toString());

        if (!results.isEmpty()) {
            throw new SonnetAlreadyExistsException("Sonnet by: " + sonnet.getLastName()
                    + "With title: " + sonnet.getTitle()
                    + " Already exists.");
        } else {
            return true;
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
        logger.debug("Searching for: " + sonnet.toString());

        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();

        Query fullTextQuery = manager.createFullTextQuery(evalFields(sonnet, queryBuilder), Sonnet.class);
        long total = fullTextQuery.getResultList().size();

        fullTextQuery.setFirstResult(toIntExact(pageRequest.getOffset())).setMaxResults(pageRequest.getPageSize());

        List<Sonnet> results;

        try {
            //noinspection unchecked
            results = fullTextQuery.getResultList();
            logger.debug("Found matching sonnets: " + total);
        } catch (NoResultException e) {
            logger.error(e);
            return null;
        }

        return new PageImpl<>(results, pageRequest, total);
    }

}
