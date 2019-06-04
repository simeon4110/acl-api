package org.acl.database.persistence.repositories;

import org.acl.database.persistence.models.web.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Stores the auth privileges.
 *
 * @author Josh Harkema
 */
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(final String name);
}
