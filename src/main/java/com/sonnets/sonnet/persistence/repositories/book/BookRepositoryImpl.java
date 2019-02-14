package com.sonnets.sonnet.persistence.repositories.book;

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
public class BookRepositoryImpl implements BookRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Optional<String> getAllBooksSimple() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_ALL_BOOKS_SIMPLE);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }

    @Override
    @Transactional
    public Optional<String> getAllBooksSimplePDO() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_ALL_BOOKS_SIMPLE_PDO);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }
}
