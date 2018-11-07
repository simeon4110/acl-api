package com.sonnets.sonnet.persistence.repositories.poem;

import com.sonnets.sonnet.persistence.models.ModelConstants;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Concrete class for handling stored procedures.
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
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public String getAllPoemsManual() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_ALL_POEMS);
        CompletableFuture.supplyAsync(query::execute);
        StringBuilder sb = new StringBuilder();
        for (Object o : query.getResultList()) {
            sb.append(o.toString());
        }
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String getRandomPoem(String form) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_RANDOM_POEM);
        query.setParameter("form", form);
        CompletableFuture.supplyAsync(query::execute);
        return String.valueOf(query.getResultList());
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public CompletableFuture<Optional<List<Poem>>> getPoemsByUser(String userName) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_POEMS_BY_USER);
        query.setParameter("userName", userName);
        return CompletableFuture.completedFuture(Optional.of((List<Poem>) query.getResultList()));
    }
}
