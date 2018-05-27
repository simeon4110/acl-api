package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Stores user details.
 *
 * @author Josh Harkema
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(final String username);
}
