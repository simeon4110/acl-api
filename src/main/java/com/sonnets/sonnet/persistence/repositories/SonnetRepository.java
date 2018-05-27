package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.Sonnet;
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

    Sonnet findByTitleAndLastName(final String title, final String lastName);

}
