package org.acl.database.persistence.repositories;

import org.acl.database.persistence.models.web.Message;
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
    List<Message> findAllByUserTo(final String to);
}
