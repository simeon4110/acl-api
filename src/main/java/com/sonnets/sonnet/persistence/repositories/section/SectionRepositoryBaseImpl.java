package com.sonnets.sonnet.persistence.repositories.section;

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
public class SectionRepositoryBaseImpl implements SectionRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Optional<String> getBookSectionsSimple(Long bookId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_BOOK_SECTIONS_SIMPLE);
        query.setParameter(StoredProcedures.BOOK_ID_PARAM, bookId);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }

    @Override
    @Transactional
    public Optional<String> getAllSectionsSimple() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_ALL_SECTIONS_SIMPLE);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }
}
