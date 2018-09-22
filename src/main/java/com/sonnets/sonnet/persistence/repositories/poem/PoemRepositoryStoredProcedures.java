package com.sonnets.sonnet.persistence.repositories.poem;

import com.sonnets.sonnet.persistence.dtos.poetry.PoemOutDto;
import com.sonnets.sonnet.persistence.models.poetry.Poem;

import java.util.List;
import java.util.Optional;

/**
 * Interface to define stored procedure methods.
 *
 * @author Josh Harkema
 */
public interface PoemRepositoryStoredProcedures {
    Optional<List<Poem>> getAllPoemsManual();

    Optional<PoemOutDto> getRandomPoem(final String form);
}
