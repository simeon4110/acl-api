package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.dtos.base.SectionOutDto;
import com.sonnets.sonnet.persistence.models.base.Section;
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
public interface SectionRepositoryBase extends JpaRepository<Section, Long> {
    @Query(value = "SELECT new com.sonnets.sonnet.persistence.dtos.base.SectionOutDto(" +
            "s.id, " +
            "s.author, " +
            "s.title, " +
            "s.parentTitle, " +
            "s.parentId) " +
            "FROM Section s " +
            "WHERE s.isPublicDomain = TRUE")
    List<SectionOutDto> getAllPublicDomain();

    @Query(value = "SELECT new com.sonnets.sonnet.persistence.dtos.base.SectionOutDto(" +
            "s.id, " +
            "s.author, " +
            "s.title, " +
            "s.parentTitle, " +
            "s.parentId) " +
            "FROM Section s")
    List<SectionOutDto> getAll();

    Optional<List<Section>> findAllByAuthor_LastName(final String lastName);

    Optional<Section> findById(final Long id);

    Optional<List<Section>> findAllByCreatedBy(final String username);
}
