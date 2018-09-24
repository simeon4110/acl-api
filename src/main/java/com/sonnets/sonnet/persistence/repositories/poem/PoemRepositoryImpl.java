package com.sonnets.sonnet.persistence.repositories.poem;

import com.sonnets.sonnet.persistence.dtos.poetry.PoemOutDto;
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
    @Async
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public CompletableFuture<Optional<List<Poem>>> getAllPoemsManual() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getAllPoemsManual");
        return CompletableFuture.completedFuture(Optional.of(query.getResultList()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PoemOutDto> getRandomPoem(String form) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getRandomPoem");
        query.setParameter("form", form);
        return Optional.of((PoemOutDto) query.getSingleResult());
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public CompletableFuture<Optional<List<Poem>>> getPoemsByUser(String userName) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getPoemsByUser");
        query.setParameter("userName", userName);
        return CompletableFuture.completedFuture(Optional.of((List<Poem>) query.getResultList()));
    }
}
