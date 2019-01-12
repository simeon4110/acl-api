package com.sonnets.sonnet.persistence.repositories.book;

import com.sonnets.sonnet.persistence.models.ModelConstants;
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
    @Transactional(readOnly = true)
    public Optional<String> getBookTitle(Long bookId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_BOOK_TITLE);
        query.setParameter(ModelConstants.BOOK_ID_PARAM, bookId);
        query.execute();
        return Optional.of((String) query.getOutputParameterValue(ModelConstants.BOOK_TITLE_PARAM));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getBooksSimple() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_BOOKS_SIMPLE);
        return Optional.of((String) query.getOutputParameterValue(ModelConstants.OUTPUT_PARAM));
    }

    @Override
    @Transactional(readOnly = true)
    public String getBookCharactersSimple(Long bookId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_BOOK_CHARACTERS);
        query.setParameter(ModelConstants.BOOK_ID_PARAM, bookId);
        return QueryHandler.queryToString(query);
    }
}
