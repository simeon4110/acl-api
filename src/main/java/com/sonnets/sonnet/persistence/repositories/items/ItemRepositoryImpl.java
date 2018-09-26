package com.sonnets.sonnet.persistence.repositories.items;

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
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getUserItems");
        query.setParameter("userName", userName);
        CompletableFuture.supplyAsync(query::execute);
        sb.append(query.getResultList());
        return sb.toString();
    }
}
