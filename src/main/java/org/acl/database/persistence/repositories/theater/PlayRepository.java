package org.acl.database.persistence.repositories.theater;

import org.acl.database.persistence.models.theater.Play;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayRepository extends JpaRepository<Play, Long> {
    boolean existsByAuthor_LastNameAndTitle(final String lastName, final String title);
}
