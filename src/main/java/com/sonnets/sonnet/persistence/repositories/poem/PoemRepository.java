package com.sonnets.sonnet.persistence.repositories.poem;

import com.sonnets.sonnet.persistence.models.poetry.Poem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
@Repository
public interface PoemRepository extends JpaRepository<Poem, Long>, PoemRepositoryStoredProcedures {

    List<Poem> findAllByForm(final String form);

    Page<Poem> findAllByForm(final String form, final Pageable pageable);

    List<Poem> findAllByCreatedBy(final String createdBy);

    Optional<List<Poem>> findAllByAuthor_LastName(final String lastName);

    Poem findFirstByConfirmation_ConfirmedAndConfirmation_PendingRevisionAndCreatedByNot(
            final boolean confirmed,
            final boolean pendingRevision,
            final String createdBy
    );

    Poem findFirstByProcessed(final boolean processed);
}
