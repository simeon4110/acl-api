package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.web.Corpora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@Repository
public interface CorporaRepository extends JpaRepository<Corpora, Long> {
    List<Corpora> findAllByCreatedBy(final String createdBy);
}
