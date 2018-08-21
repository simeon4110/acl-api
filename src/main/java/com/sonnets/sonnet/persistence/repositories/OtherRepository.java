package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.prose.Other;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherRepository extends JpaRepository<Other, Long> {
    Other findByAuthor_LastNameAndTitle(final String lastName, final String title);
}
