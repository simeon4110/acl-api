package com.sonnets.sonnet.repositories;

import com.sonnets.sonnet.models.Sonnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Basic repo, custom searching queries should be defined here.
 *
 * @author Josh Harkema
 */
@Repository
public interface SonnetRepository extends JpaRepository<Sonnet, Long> {
    // Empty by design.
}
