package org.acl.database.persistence.repositories;

import org.acl.database.persistence.models.web.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repo interface for the User table.
 *
 * @author Josh Harkema
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(final String username);
}
