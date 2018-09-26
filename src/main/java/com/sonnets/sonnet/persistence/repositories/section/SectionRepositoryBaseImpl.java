package com.sonnets.sonnet.persistence.repositories.section;

import com.sonnets.sonnet.persistence.models.prose.Section;
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
public class SectionRepositoryBaseImpl implements SectionRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Optional<List<Section>> getAllSections() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getAllSections");
        return Optional.of(query.getResultList());
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public CompletableFuture<Optional<List<Section>>> getAllByUser(String userName) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getSectionsByUser");
        query.setParameter("userName", userName);
        return CompletableFuture.completedFuture(
                Optional.of((List<Section>) query.getResultList())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getBookSectionsSimple(Long bookId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getBookSectionsSimple");
        query.setParameter("bookId", bookId);
        return Optional.of((String) query.getOutputParameterValue("output"));
    }

    @Override
    @Transactional
    public void updateSectionAnnotation(String annotation, Long annotationId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("updateSectionAnnotation");
        query.setParameter("annotation", annotation);
        query.setParameter("annotationId", annotationId);
        query.execute();
    }
}
