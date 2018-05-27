package com.sonnets.sonnet.repositories;

import com.sonnets.sonnet.models.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Stores the auth privileges.
 *
 * @author Josh Harkema
 */
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(final String name);
}
