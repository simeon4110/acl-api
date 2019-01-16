package com.sonnets.sonnet.persistence.repositories.corpora;

import com.sonnets.sonnet.persistence.models.StoredProcedures;
import com.sonnets.sonnet.persistence.models.web.Corpora;
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
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.ADD_CORPORA_ITEM);
        query.setParameter(StoredProcedures.CORPORA_ID, corporaId);
        query.setParameter(StoredProcedures.ITEM_ID, itemId);
        query.setParameter(StoredProcedures.ITEM_TYPE, itemType);
        query.execute();
    }

    @Override
    @Async
    @Transactional
    public void removeCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.DELETE_CORPORA_ITEM);
        query.setParameter(StoredProcedures.CORPORA_ID, corporaId);
        query.setParameter(StoredProcedures.ITEM_ID, itemId);
        query.setParameter(StoredProcedures.ITEM_TYPE, itemType);
        query.execute();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Corpora> getCorpora(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_CORPORA);
        query.setParameter(StoredProcedures.CORPORA_ID, corporaId);
        return Optional.of((Corpora) query.getSingleResult());
    }

    @Override
    @Transactional(readOnly = true)
    public String getCorporaItems(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_CORPORA_ITEMS);
        query.setParameter(StoredProcedures.CORPORA_ID, corporaId);
        return QueryHandler.queryToString(query);
    }

    @Override
    @Transactional(readOnly = true)
    public String getCorporaItemsSimple(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_CORPORA_ITEMS_SIMPLE);
        query.setParameter(StoredProcedures.CORPORA_ID, corporaId);
        return QueryHandler.queryToString(query);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List> getCorporaUser(String createdBy) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedures.GET_CORPORA_USER);
        query.setParameter(StoredProcedures.CREATED_BY_PARAM, createdBy);
        return Optional.of(query.getResultList());
    }
}
