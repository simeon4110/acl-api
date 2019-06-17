package org.acl.database.persistence.repositories;

import org.acl.database.persistence.dtos.base.ShortStoryOutDto;
import org.acl.database.persistence.models.base.ShortStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
public interface ShortStoryRepository extends JpaRepository<ShortStory, Long> {

    @Query(value = "SELECT new org.acl.database.persistence.dtos.base.ShortStoryOutDto(" +
            "s.id, " +
            "s.author, " +
            "s.title, " +
            "s.sourceTitle, " +
            "s.category) " +
            "FROM ShortStory s " +
            "WHERE s.isPublicDomain = TRUE")
    List<ShortStoryOutDto> getAllPublicDomain();

    @Query(value = "SELECT new org.acl.database.persistence.dtos.base.ShortStoryOutDto(" +
            "s.id, " +
            "s.author, " +
            "s.title, " +
            "s.sourceTitle, " +
            "s.category) " +
            "FROM ShortStory s")
    List<ShortStoryOutDto> getAll();

    Optional<ShortStory> findById(final Long id);

    Optional<List<ShortStory>> findAllByIsPublicDomain(final Boolean isPublicDomain);

    Optional<List<ShortStory>> findAllByCreatedBy(final String username);
}
