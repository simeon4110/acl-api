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

import static java.lang.Math.toIntExact;

/**
 * VERY basic search impl. Can be extended later.
 *
 * @author Josh Harkema
 */
@Repository
@Transactional
public class SearchService {
    private static final Logger logger = Logger.getLogger(SearchService.class);
    @PersistenceContext
    private EntityManager entityManager;

    public PageImpl<Sonnet> search(Sonnet sonnet, Pageable pageRequest) {
        logger.debug("Searching for: " + sonnet.toString());

        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Sonnet.class).get();

        // Fuzzy query isn't super strict and returns many sonnets. It's a feature, not a bug.
        org.apache.lucene.search.Query luceneQuery = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1)
                .withPrefixLength(1).onFields("firstName", "lastName", "title", "publicationYear", "text")
                .matching(sonnet.toString()).createQuery();


        Query fullTextQuery = manager.createFullTextQuery(luceneQuery, Sonnet.class);
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

        return new PageImpl<Sonnet>(results, pageRequest, total);
    }
}
