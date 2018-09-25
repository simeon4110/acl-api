package com.sonnets.sonnet.persistence.repositories.corpora;

import com.sonnets.sonnet.persistence.dtos.base.ItemOutSimpleDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.HashSet;
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

    @Override
    @Async
    @Transactional
    public void addCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("addCorporaItem");
        query.setParameter("corporaId", corporaId);
        query.setParameter("itemId", itemId);
        query.setParameter("itemType", itemType);
        query.execute();
    }

    @Override
    @Async
    @Transactional
    public void removeCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("deleteCorporaItem");
        query.setParameter("corporaId", corporaId);
        query.setParameter("itemId", itemId);
        query.setParameter("itemType", itemType);
        query.execute();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Corpora> getCorpora(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorpora");
        query.setParameter("corporaId", corporaId);
        return Optional.of((Corpora) query.getSingleResult());
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public String getCorporaItems(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaItems");
        query.setParameter("corporaId", corporaId);
        CompletableFuture.supplyAsync(query::execute);
        StringBuilder sb = new StringBuilder();
        sb.append(query.getResultList());
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    @Async
    @SuppressWarnings("unchecked")
    public CompletableFuture<Optional<HashSet<ItemOutSimpleDto>>> getCorporaItemsSimple(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaItemsSimple");
        query.setParameter("corporaId", corporaId);
        return CompletableFuture.completedFuture(Optional.of(new HashSet<ItemOutSimpleDto>(query.getResultList())));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List> getCorporaUser(String createdBy) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaUser");
        query.setParameter("createdBy", createdBy);
        return Optional.of(query.getResultList());
    }
}
