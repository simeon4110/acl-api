package com.sonnets.sonnet.persistence.repositories.book;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Concrete class for handling stored procedures.
 *
 * @author Josh Harkema
 */
@Repository
@Transactional
public class BookRepositoryImpl implements BookRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getBookTitle(Long bookId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getBookTitle");
        query.setParameter("bookId", bookId);
        query.execute();
        return Optional.of((String) query.getOutputParameterValue("title"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getBooksSimple() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getBooksSimple");
        return Optional.of((String) query.getOutputParameterValue("output"));
    }

    @Override
    @Transactional(readOnly = true)
    public String getBookCharactersSimple(Long bookId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getBookCharacters");
        query.setParameter("bookId", bookId);
        CompletableFuture.supplyAsync(query::execute);
        StringBuilder sb = new StringBuilder();
        sb.append(query.getResultList());
        return sb.toString();
    }
}