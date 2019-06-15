package org.acl.database.persistence.repositories.theater;

import org.acl.database.persistence.models.theater.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
}
