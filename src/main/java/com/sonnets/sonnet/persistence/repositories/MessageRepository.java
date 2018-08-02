package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Abstract message repo.
 *
 * @author Josh Harkema
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByUserFrom(final String from);

    List<Message> findAllByUserTo(final String to);

    List<Message> findAllByUserToAndIsRead(final String to, final boolean isRead);
}
