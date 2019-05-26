package com.sonnets.sonnet.persistence.repositories.poem;

import com.sonnets.sonnet.persistence.dtos.base.PoemOutDto;
import com.sonnets.sonnet.persistence.models.base.Poem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
@Repository
public interface PoemRepository extends JpaRepository<Poem, Long>, PoemRepositoryStoredProcedures {
    @Query(value = "SELECT new com.sonnets.sonnet.persistence.dtos.base.PoemOutDto(" +
            "p.id, " +
            "p.author, " +
            "p.title, " +
            "p.sourceTitle, " +
            "p.period, " +
            "p.form) " +
            "FROM Poem p " +
            "WHERE p.isPublicDomain = TRUE")
    List<PoemOutDto> getAllPublicDomain();

    @Query(value = "SELECT new com.sonnets.sonnet.persistence.dtos.base.PoemOutDto(" +
            "p.id, " +
            "p.author, " +
            "p.title, " +
            "p.sourceTitle, " +
            "p.period, " +
            "p.form) " +
            "FROM Poem p")
    List<PoemOutDto> getAll();

    Optional<List<Poem>> findAllByForm(final String form);

    Optional<List<Poem>> findAllByHidden(final boolean hidden);

    Optional<Page<Poem>> findAllByForm(final String form, final Pageable pageable);

    Optional<List<Poem>> findAllByCreatedBy(final String createdBy);

    Optional<List<Poem>> findAllByAuthor_LastName(final String lastName);

    Optional<Page<Poem>> findAllByIsPublicDomain(final Boolean isPublicDomain, Pageable pageable);

    Long countAllByCreatedByAndConfirmation_PendingRevision(final String createdBy, final Boolean pendingRevision);
}
