package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.web.Corpora;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
@Repository
public interface CorporaRepository extends CrudRepository<Corpora, Long> {

    List<Corpora> findAllByCreatedBy(final String createdBy);
}
