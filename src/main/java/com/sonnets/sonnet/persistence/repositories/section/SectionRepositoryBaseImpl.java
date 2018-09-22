package com.sonnets.sonnet.persistence.repositories.section;

import com.sonnets.sonnet.persistence.models.prose.Section;
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
public class SectionRepositoryBaseImpl implements SectionRepositoryStoredProcedures {
    @PersistenceContext
    EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<Section>> getAllSections() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getAllSections");
        return Optional.of(query.getResultList());
    }
}
