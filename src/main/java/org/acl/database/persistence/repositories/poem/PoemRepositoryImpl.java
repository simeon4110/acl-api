package org.acl.database.persistence.repositories.poem;

import org.acl.database.persistence.models.StoredProcedureConstants;
import org.acl.database.tools.QueryHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Optional;

/**
 * Concrete class for stored procedures.
 *
 * @author Josh Harkema
 */
@Repository
@Transactional
public class PoemRepositoryImpl implements PoemRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Optional<String> getTwoRandomPoems() {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedureConstants.GET_TWO_RANDOM_POEMS);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }

    @Override
    @Transactional
    public Optional<String> getPoemToConfirm(String username) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(StoredProcedureConstants.GET_POEM_TO_CONFIRM);
        query.setParameter(StoredProcedureConstants.USER_NAME_PARAM, username);
        return Optional.ofNullable(QueryHandler.queryToString(query, true));
    }
}
