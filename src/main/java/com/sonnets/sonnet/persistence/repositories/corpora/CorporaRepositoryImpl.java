package com.sonnets.sonnet.persistence.repositories.corpora;

import com.sonnets.sonnet.persistence.models.web.Corpora;
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
public class CorporaRepositoryImpl implements CorporaRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    private static final String CORPORA_ID = "corporaId";

    @Override
    @Async
    @Transactional
    public void addCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("addCorporaItem");
        query.setParameter(CORPORA_ID, corporaId);
        query.setParameter("itemId", itemId);
        query.setParameter("itemType", itemType);
        query.execute();
    }

    @Override
    @Async
    @Transactional
    public void removeCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("deleteCorporaItem");
        query.setParameter(CORPORA_ID, corporaId);
        query.setParameter("itemId", itemId);
        query.setParameter("itemType", itemType);
        query.execute();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Corpora> getCorpora(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorpora");
        query.setParameter(CORPORA_ID, corporaId);
        return Optional.of((Corpora) query.getSingleResult());
    }

    @Override
    @Transactional(readOnly = true)
    public String getCorporaItems(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaItems");
        query.setParameter(CORPORA_ID, corporaId);
        CompletableFuture.supplyAsync(query::execute);
        return String.valueOf(query.getResultList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getCorporaItemsSimple(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaItemsSimple");
        query.setParameter(CORPORA_ID, corporaId);
        CompletableFuture.supplyAsync(query::execute);
        StringBuilder sb = new StringBuilder();
        for (Object o : query.getResultList()) {
            sb.append(o.toString());
        }
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List> getCorporaUser(String createdBy) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaUser");
        query.setParameter("createdBy", createdBy);
        return Optional.of(query.getResultList());
    }
}
