package com.sonnets.sonnet.persistence.repositories.poem;

import com.sonnets.sonnet.persistence.models.StoredProcedures;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tools.QueryHandler;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Optional;

/**
 * Concrete class for stored procedures.
 *
 * @author Josh Harkema
 */
@Repository
@Transactional
public class PoemRepositoryImpl implements PoemRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Optional<String> getAllPoemsSimple() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_ALL_POEMS_SIMPLE);
        query.execute();
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }
}
