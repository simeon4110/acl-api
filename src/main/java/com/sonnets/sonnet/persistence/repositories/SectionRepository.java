package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.prose.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Josh Harkema
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
}
