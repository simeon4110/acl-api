package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.base.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Josh Harkema
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByLastName(final String lastName);

    Author findByLastNameAndFirstName(final String lastName, final String firstName);
}
