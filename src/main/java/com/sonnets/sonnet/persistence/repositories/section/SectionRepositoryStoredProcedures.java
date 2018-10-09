package com.sonnets.sonnet.persistence.repositories.section;

import com.sonnets.sonnet.persistence.models.prose.Section;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Interface to define stored procedure methods.
 *
 * @author Josh Harkema
 */
public interface SectionRepositoryStoredProcedures {
    Optional<List<Section>> getAllSections();

    CompletableFuture<Optional<List<Section>>> getAllByUser(final String userName);

    Optional<String> getBookSectionsSimple(final Long bookId);

    void updateSectionAnnotation(final String annotation, final Long annotationId, final String userName);
}
