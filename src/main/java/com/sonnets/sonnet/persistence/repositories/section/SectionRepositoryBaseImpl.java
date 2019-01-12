package com.sonnets.sonnet.persistence.repositories.section;

import com.sonnets.sonnet.persistence.models.ModelConstants;
import com.sonnets.sonnet.persistence.models.prose.Section;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tools.QueryHandler;

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
public class SectionRepositoryBaseImpl implements SectionRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public String getAllSections() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_ALL_SECTIONS);
        CompletableFuture.supplyAsync(query::execute);
        return QueryHandler.queryToString(query);
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public CompletableFuture<Optional<List<Section>>> getAllByUser(String userName) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_SECTIONS_BY_USER);
        query.setParameter(ModelConstants.USER_NAME_PARAM, userName);
        return CompletableFuture.completedFuture(
                Optional.of((List<Section>) query.getResultList())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getBookSectionsSimple(Long bookId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_BOOK_SECTIONS_SIMPLE);
        query.setParameter(ModelConstants.BOOK_ID_PARAM, bookId);
        return Optional.of((String) query.getOutputParameterValue(ModelConstants.OUTPUT_PARAM));
    }
}
