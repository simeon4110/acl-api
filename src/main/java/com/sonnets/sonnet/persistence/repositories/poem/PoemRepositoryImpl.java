package com.sonnets.sonnet.persistence.repositories.poem;

import com.sonnets.sonnet.persistence.dtos.poetry.PoemOutDto;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import org.springframework.stereotype.Repository;

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
public class PoemRepositoryImpl implements PoemRepositoryStoredProcedures {
    @PersistenceContext
    EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<Poem>> getAllPoemsManual() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getAllPoemsManual");
        return Optional.of(query.getResultList());
    }

    @Override
    public Optional<PoemOutDto> getRandomPoem(String form) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getRandomPoem");
        query.setParameter("form", form);
        return Optional.of((PoemOutDto) query.getSingleResult());
    }
}
