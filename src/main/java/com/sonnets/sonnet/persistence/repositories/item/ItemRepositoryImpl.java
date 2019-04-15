package com.sonnets.sonnet.persistence.repositories.item;

import com.sonnets.sonnet.persistence.models.StoredProcedureConstants;
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
public class ItemRepositoryImpl implements ItemRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Optional<String> getAllItems() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedureConstants.GET_ALL_ITEMS);
        return Optional.ofNullable(QueryHandler.queryToString(query));
    }
}
