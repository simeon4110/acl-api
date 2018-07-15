package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.Corpera;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
public interface CorperaRepository extends JpaRepository<Corpera, Long> {
    // empty by design
}
