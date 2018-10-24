package com.sonnets.sonnet.persistence.repositories.items;

import com.sonnets.sonnet.persistence.models.ModelConstants;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.concurrent.CompletableFuture;

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
    @SuppressWarnings("unchecked")
    public String getItemsByUser(String userName) {
        StringBuilder sb = new StringBuilder();
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_USER_ITEMS);
        query.setParameter("userName", userName);
        CompletableFuture.supplyAsync(query::execute);
        for (Object o : query.getResultList()) {
            sb.append(o.toString());
        }
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String getAllItems() {
        StringBuilder sb = new StringBuilder();
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_ALL_ITEMS);
        CompletableFuture.supplyAsync(query::execute);
        for (Object o : query.getResultList()) {
            sb.append(o.toString());
        }
        return sb.toString();
    }
}
