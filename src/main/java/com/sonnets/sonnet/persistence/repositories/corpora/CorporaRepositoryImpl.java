package com.sonnets.sonnet.persistence.repositories.corpora;

import com.sonnets.sonnet.persistence.models.ModelConstants;
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

    @Override
    @Async
    @Transactional
    public void addCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.ADD_CORPORA_ITEM);
        query.setParameter(ModelConstants.CORPORA_ID, corporaId);
        query.setParameter(ModelConstants.ITEM_ID, itemId);
        query.setParameter(ModelConstants.ITEM_TYPE, itemType);
        query.execute();
    }

    @Override
    @Async
    @Transactional
    public void removeCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.DELETE_CORPORA_ITEM);
        query.setParameter(ModelConstants.CORPORA_ID, corporaId);
        query.setParameter(ModelConstants.ITEM_ID, itemId);
        query.setParameter(ModelConstants.ITEM_TYPE, itemType);
        query.execute();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Corpora> getCorpora(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_CORPORA);
        query.setParameter(ModelConstants.CORPORA_ID, corporaId);
        return Optional.of((Corpora) query.getSingleResult());
    }

    @Override
    @Transactional(readOnly = true)
    public String getCorporaItems(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_CORPORA_ITEMS);
        query.setParameter(ModelConstants.CORPORA_ID, corporaId);
        CompletableFuture.supplyAsync(query::execute);
        StringBuilder sb = new StringBuilder();
        for (Object o : query.getResultList()) {
            sb.append(o.toString());
        }
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String getCorporaItemsSimple(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_CORPORA_ITEMS_SIMPLE);
        query.setParameter(ModelConstants.CORPORA_ID, corporaId);
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
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ModelConstants.GET_CORPORA_USER);
        query.setParameter(ModelConstants.CREATED_BY, createdBy);
        return Optional.of(query.getResultList());
    }
}
