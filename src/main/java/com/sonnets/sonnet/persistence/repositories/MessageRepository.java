package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.MessageImpl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Abstract message repo.
 *
 * @author Josh Harkema
 */
public interface MessageRepository extends JpaRepository<MessageImpl, Long> {
    List<MessageImpl> findAllByFromUser(final String from);

    List<MessageImpl> findAllByToUser(final String to);
}
