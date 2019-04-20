package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.web.Corpora;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@Repository
public interface CorporaRepository extends CrudRepository<Corpora, Long> {
}
