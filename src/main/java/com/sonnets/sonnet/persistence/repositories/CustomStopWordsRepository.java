package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.tools.CustomStopWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Josh Harkema
 */
@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
@Repository
public interface CustomStopWordsRepository extends JpaRepository<CustomStopWords, Long> {
    List<CustomStopWords> findAllByCreatedBy(final String username);
}
