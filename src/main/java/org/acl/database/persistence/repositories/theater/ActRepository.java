package org.acl.database.persistence.repositories.theater;

import org.acl.database.persistence.models.theater.Act;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActRepository extends JpaRepository<Act, Long> {
}
