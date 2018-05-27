package com.sonnets.sonnet.repositories;

import com.sonnets.sonnet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Stores user details.
 *
 * @author Josh Harkema
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(final String username);
}
