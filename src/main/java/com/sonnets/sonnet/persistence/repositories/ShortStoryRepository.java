package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.base.ShortStory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
public interface ShortStoryRepository extends JpaRepository<ShortStory, Long> {
    Optional<ShortStory> findById(final Long id);

    Optional<List<ShortStory>> findAllByIsPublicDomain(final Boolean isPublicDomain);

    Optional<List<ShortStory>> findAllByCreatedBy(final String username);
}
