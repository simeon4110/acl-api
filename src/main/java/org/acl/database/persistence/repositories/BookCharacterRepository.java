package org.acl.database.persistence.repositories;

import org.acl.database.persistence.models.prose.BookCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Josh Harkema
 */
@Repository
public interface BookCharacterRepository extends JpaRepository<BookCharacter, Long> {
}
