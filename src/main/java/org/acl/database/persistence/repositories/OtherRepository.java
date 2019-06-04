package org.acl.database.persistence.repositories;

import org.acl.database.persistence.models.base.Other;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherRepository extends JpaRepository<Other, Long> {
    Other findByAuthor_LastNameAndTitle(final String lastName, final String title);
}
