package org.acl.database.persistence.repositories;

import org.acl.database.persistence.models.web.MailingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repo interface for mailing list entries.
 *
 * @author Josh Harkema
 */
@Repository
public interface MailingListRepository extends JpaRepository<MailingList, Long> {
    MailingList findByEmail(final String email);
}
