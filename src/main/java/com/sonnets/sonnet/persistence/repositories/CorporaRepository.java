package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.dtos.web.CorporaBasicOutDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@Repository
public interface CorporaRepository extends CrudRepository<Corpora, Long> {
    @Query(value = "SELECT new com.sonnets.sonnet.persistence.dtos.web.CorporaBasicOutDto(" +
            "c.id, " +
            "c.name, " +
            "c.description, " +
            "c.totalItems) " +
            "FROM Corpora c " +
            "WHERE c.id = ?1")
    Optional<CorporaBasicOutDto> getByIdBasic(final Long id);
}
