package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.Sonnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repo interface for the Sonnet table.
 *
 * @author Josh Harkema
 */
@Repository
public interface SonnetRepository extends JpaRepository<Sonnet, Long> {
    List<Sonnet> findAllByLastName(final String lastName);

    List<Sonnet> findAllByFirstName(final String firstName);

    Sonnet findByTitleAndLastName(final String title, final String lastName);

    List<Sonnet> findAllByAddedBy(final String addedBy);
}
