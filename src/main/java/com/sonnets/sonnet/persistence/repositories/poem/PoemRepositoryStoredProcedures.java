package com.sonnets.sonnet.persistence.repositories.poem;

import java.util.Optional;

public interface PoemRepositoryStoredProcedures {
    Optional<String> getAllPoemsSimple();
}
