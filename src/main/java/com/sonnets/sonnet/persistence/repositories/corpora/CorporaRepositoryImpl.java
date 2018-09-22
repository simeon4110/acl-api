package com.sonnets.sonnet.persistence.repositories.corpora;

import com.sonnets.sonnet.persistence.dtos.base.ItemOutDto;
import com.sonnets.sonnet.persistence.dtos.base.ItemOutSimpleDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Concrete class for handling stored procedures.
 *
 * @author Josh Harkema
 */
@Repository
public class CorporaRepositoryImpl implements CorporaRepositoryStoredProcedures {
    @PersistenceContext
    EntityManager em;

    @Override
    public int countCorporaItems(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("countCorporaItems");
        query.setParameter("corporaId", corporaId);
        query.execute();
        return (int) query.getOutputParameterValue("itemCount");
    }

    @Override
    public void setCorporaItemsCount(Long corporaId, int count) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("updateCorporaItemCount");
        query.setParameter("corporaId", corporaId);
        query.setParameter("count", count);
        query.execute();
    }

    @Override
    public void addCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("addCorporaItem");
        query.setParameter("corporaId", corporaId);
        query.setParameter("itemId", itemId);
        query.setParameter("itemType", itemType);
        query.execute();
    }

    @Override
    public void removeCorporaItem(Long corporaId, Long itemId, String itemType) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("deleteCorporaItem");
        query.setParameter("corporaId", corporaId);
        query.setParameter("itemId", itemId);
        query.setParameter("itemType", itemType);
        query.execute();
    }

    @Override
    public Optional<Corpora> getCorpora(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorpora");
        query.setParameter("corporaId", corporaId);
        return Optional.of((Corpora) query.getSingleResult());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Set<ItemOutDto>> getCorporaItems(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaItems");
        query.setParameter("corporaId", corporaId);
        return Optional.of(new HashSet<ItemOutDto>(query.getResultList()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Set<ItemOutSimpleDto>> getCorporaItemsSimple(Long corporaId) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaItemsSimple");
        query.setParameter("corporaId", corporaId);
        return Optional.of(new HashSet<ItemOutSimpleDto>(query.getResultList()));
    }

    @Override
    public Optional<List> getCorporaUser(String createdBy) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getCorporaUser");
        query.setParameter("createdBy", createdBy);
        return Optional.of(query.getResultList());
    }
}
