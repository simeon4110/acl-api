package com.sonnets.sonnet.services;

import com.sonnets.sonnet.models.Sonnet;
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

        if (sonnet.getFirstName() != null && !Objects.equals(sonnet.getFirstName(), "")) {
            logger.debug(sonnet.getFirstName());
            query = queryBuilder.keyword().fuzzy().withPrefixLength(0).withEditDistanceUpTo(1).onField("firstName")
                    .matching(sonnet.getFirstName()).createQuery();
        }

        if (sonnet.getLastName() != null && !Objects.equals(sonnet.getLastName(), "")) {
            logger.debug(sonnet.getLastName());
            query = queryBuilder.keyword().fuzzy().withPrefixLength(0).withEditDistanceUpTo(1).onField("lastName")
                    .matching(sonnet.getLastName()).createQuery();
        }

        if (sonnet.getTitle() != null && !Objects.equals(sonnet.getTitle(), "")) {
            logger.debug(sonnet.getTitle());
            query = queryBuilder.keyword().fuzzy().withPrefixLength(0).withEditDistanceUpTo(1).onField("title")
                    .matching(sonnet.getTitle()).createQuery();
        }

        if (sonnet.getPublicationYear() != null && !Objects.equals(sonnet.getPublicationYear(), "")) {
            logger.debug(sonnet.getPublicationYear());
            query = queryBuilder.keyword().fuzzy().withPrefixLength(0).withEditDistanceUpTo(1)
                    .onField("publicationYear").matching(sonnet.getPublicationYear()).createQuery();
        }

        if (sonnet.getText() != null && !sonnet.getText().isEmpty()) {
            logger.debug(sonnet.getText());
            query = queryBuilder.phrase().onField("text").sentence(sonnet.getText().toString()).createQuery();
        }

        if (query == null) {
            query = queryBuilder.all().createQuery();
        }

        return query;
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

        List<Sonnet> results = null;

        try {
            //noinspection unchecked
            results = fullTextQuery.getResultList();
            logger.debug("Found matching sonnets: " + total);

        } catch (NoResultException e) {
            logger.error(e);
            total = 0;
        }

        return new PageImpl<>(results, pageRequest, total);
    }
}
