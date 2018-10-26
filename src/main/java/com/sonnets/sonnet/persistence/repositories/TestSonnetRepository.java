package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.poetry.TestSonnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repo for testing sonnets.
 *
 * @author Josh Harkema
 */
@Repository
public interface TestSonnetRepository extends JpaRepository<TestSonnet, Long> {
    // empty by design.
}
