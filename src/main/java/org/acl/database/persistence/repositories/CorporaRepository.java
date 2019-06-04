package org.acl.database.persistence.repositories;

import org.acl.database.persistence.dtos.web.CorporaBasicOutDto;
import org.acl.database.persistence.models.web.Corpora;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@Repository
public interface CorporaRepository extends CrudRepository<Corpora, Long> {
    @Query(value = "SELECT new org.acl.database.persistence.dtos.web.CorporaBasicOutDto(" +
            "c.id, " +
            "c.name, " +
            "c.description, " +
            "c.totalItems) " +
            "FROM Corpora c " +
            "WHERE c.id = ?1")
    Optional<CorporaBasicOutDto> getByIdBasic(final Long id);

    @SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
    Optional<List<Corpora>> getAllByCreatedBy(final String createdBy);
}
