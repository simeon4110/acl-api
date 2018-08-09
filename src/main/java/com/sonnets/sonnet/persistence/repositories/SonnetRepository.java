package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.Sonnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repo interface for the Sonnet table.
 *
 * @author Josh Harkema
 */
@Repository
public interface SonnetRepository extends JpaRepository<Sonnet, Long> {
    List<Sonnet> findAllByLastName(final String lastName);

    Sonnet findFirstByConfirmedAndPendingRevision(final boolean confirmed, final boolean pendingRevision);

    List<Sonnet> findAllByCreatedBy(final String createdBy);

    List<Sonnet> findAllByCreatedByAndConfirmedAtBetween(final String createdBy, final Date before, final Date after);
}
