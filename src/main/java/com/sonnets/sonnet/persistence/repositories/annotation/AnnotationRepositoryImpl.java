package com.sonnets.sonnet.persistence.repositories.annotation;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This interface defines methods that can span annotation types.
 *
 * @author Josh Harkema
 */
@Repository
@Transactional
public class AnnotationRepositoryImpl implements AnnotationRepositoryStoredProcedures {
    @Resource
    @PersistenceContext
    EntityManager em;
}
