package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.poetry.Poem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoemRepository extends JpaRepository<Poem, Long> {
    List<Poem> findAllByForm(final Poem.Form form);

    Page<Poem> findAllByForm(final Poem.Form form, final Pageable pageable);

    List<Poem> findAllByCreatedBy(final String createdBy);

    List<Poem> findAllByConfirmation_ConfirmedAndConfirmation_PendingRevision(final boolean confirmed,
                                                                              final boolean pendingRevision);
}
