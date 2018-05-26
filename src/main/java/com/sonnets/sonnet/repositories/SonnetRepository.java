package com.sonnets.sonnet.repositories;

import com.sonnets.sonnet.models.Sonnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Basic repo, custom searching queries should be defined here.
 *
 * @author Josh Harkema
 */
@Repository
public interface SonnetRepository extends JpaRepository<Sonnet, Long> {
    List<Sonnet> findAllByLastName(final String lastName);

    List<Sonnet> findAllByFirstName(final String firstName);

    List<Sonnet> findAllByLastNameStartingWith(final char startsWith);
}
