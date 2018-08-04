package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.Corpera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@Repository
public interface CorperaRepository extends JpaRepository<Corpera, Long> {
    List<Corpera> findAllByUsername(final String username);
}
