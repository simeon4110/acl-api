package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Stores the auth privileges.
 *
 * @author Josh Harkema
 */
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(final String name);
}
