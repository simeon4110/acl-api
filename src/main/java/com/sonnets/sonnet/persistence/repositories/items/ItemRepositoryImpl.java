package com.sonnets.sonnet.persistence.repositories.items;

import com.sonnets.sonnet.persistence.models.ModelConstants;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tools.QueryHandler;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

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
    @Transactional(readOnly = true)
    public String getItemsByUser(String userName) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_USER_ITEMS);
        query.setParameter(ModelConstants.USER_NAME_PARAM, userName);
        return QueryHandler.queryToString(query);
    }

    @Override
    @Transactional(readOnly = true)
    public String getAllItems() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_ALL_ITEMS);
        return QueryHandler.queryToString(query);
    }
}
