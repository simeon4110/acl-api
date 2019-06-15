package org.acl.database.services.theater;

import org.acl.database.persistence.dtos.theater.ActorDto;
import org.acl.database.persistence.models.theater.Actor;
import org.acl.database.persistence.repositories.theater.ActorRepository;
import org.acl.database.security.UserDetailsServiceImpl;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Service handles all actor related operations.
 *
 * @author Josh Harkema
 */
@Service
public class ActorService {
    private static final Logger LOGGER = Logger.getLogger(ActorService.class);
    private final ActorRepository actorRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public ActorService(ActorRepository actorRepository, UserDetailsServiceImpl userDetailsService) {
        this.actorRepository = actorRepository;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Add and persist a new Actor Object.
     *
     * @param dto the dto with the new Author's details.
     * @return 201 if good.
     */
    public ResponseEntity<Void> add(final ActorDto dto) {
        LOGGER.debug("Adding new actor: " + dto.toString());
        Actor actor = new Actor(dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getNotes());
        actorRepository.saveAndFlush(actor);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Get an Actor object from its database ID.
     *
     * @param id the id of the object.
     * @return the object if found, 404 if Actor with 'id' does not exist.
     */
    public Actor getById(final Long id) {
        LOGGER.debug("Returning actor: " + id);
        return actorRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Modify an existing Actor object.
     *
     * @param dto       the dto with the updated details.
     * @param principal of the user making the request.
     * @return 204 if good, 401 if user is not authorized.
     */
    public ResponseEntity<Void> modify(final ActorDto dto, final Principal principal) {
        LOGGER.debug("Modifying actor: " + dto.toString());
        Actor actor = actorRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        if (principal.getName().equals(actor.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            actor.setFirstName(dto.getFirstName());
            actor.setMiddleName(dto.getMiddleName());
            actor.setLastName(dto.getLastName());
            actor.setNotes(dto.getNotes());
            actorRepository.saveAndFlush(actor);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Delete an existing Actor object.
     *
     * @param id        the id of the Actor to delete.
     * @param principal of the user making the request.
     * @return 204 if good, 401 if user is not authorized.
     */
    public ResponseEntity<Void> delete(final Long id, final Principal principal) {
        LOGGER.debug("Deleting actor: " + id);
        Actor actor = actorRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (principal.getName().equals(actor.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            actorRepository.delete(actor);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
