package com.sonnets.sonnet.persistence.repositories.item;

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
 * Concrete class for handling stored procedures.
 *
 * @author Josh Harkema
 */
@Repository
@Transactional
public class ItemRepositoryImpl implements ItemRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Optional<String> getAllUserItems(String userName) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_ALL_USER_ITEMS);
        query.setParameter(StoredProcedures.USER_NAME_PARAM, userName);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }

    @Override
    @Transactional
    public Optional<String> getAllItems() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_ALL_ITEMS);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }
}
