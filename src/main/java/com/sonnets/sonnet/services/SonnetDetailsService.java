package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.persistence.dtos.sonnet.RejectDto;
import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.exceptions.SonnetAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.persistence.repositories.SonnetRepository;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Basic service to interface with SonnetRepository. More search and analytics will be added here. All view / database
 * interfacing must travel through this service.
 *
 * @author Josh Harkema
 */
@Service
public class SonnetDetailsService {
    private final SonnetRepository sonnetRepository;
    private final SearchService searchService;
    private final UserDetailsServiceImpl userDetailsService;
    private final MessageService messageService;
    private static final Logger LOGGER = Logger.getLogger(SonnetDetailsService.class);
    private static final int NUMBER_OF_RANDOM_SONNETS = 2;

    @Autowired
    public SonnetDetailsService(SonnetRepository sonnetRepository, SearchService searchService,
                                UserDetailsServiceImpl userDetailsService, MessageService messageService) {
        this.sonnetRepository = sonnetRepository;
        this.searchService = searchService;
        this.userDetailsService = userDetailsService;
        this.messageService = messageService;
    }

    /**
     * Add a new Sonnet object from a @Valid SonnetDto.
     *
     * @param newSonnet a valid SonnetDto object.
     * @return the Sonnet object created.
     */
    public ResponseEntity<Void> addNewSonnet(SonnetDto newSonnet, Principal principal) {
        LOGGER.debug("Adding sonnet: " + "'" + newSonnet + "'");
        try {
            if (!sonnetRepository.findAll().isEmpty()) {
                searchService.similarExists(newSonnet);
            }
            Sonnet toAddSonnet = new Sonnet(newSonnet);
            sonnetRepository.saveAndFlush(toAddSonnet);

            // Sets a user's ability to confirm sonnets if they've passed the required threshold.
            User user = userDetailsService.loadUserObjectByUsername(principal.getName());
            if (user.getRequiredSonnets() <= sonnetRepository.findAllByCreatedBy(principal.getName()).size() &&
                    !user.getCanConfirm()) {
                user.setCanConfirm(true);
                userDetailsService.save(user);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (SonnetAlreadyExistsException e) {
            LOGGER.error(e);

            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * Updates an existing sonnet.
     *
     * @param updateSonnet the SonnetDto of the new data.
     * @return the sonnet with the updated data.
     */
    public ResponseEntity<Void> updateSonnet(SonnetDto updateSonnet) {
        LOGGER.debug("Updating sonnet: " + updateSonnet.toString());
        Sonnet sonnet = getSonnetOrError(updateSonnet.getId().toString());

        if (sonnet != null) {
            sonnet.update(updateSonnet);
            sonnetRepository.saveAndFlush(sonnet);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Sonnet does not exist with id: " + updateSonnet.getId());

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @return two randomly selected sonnets chosen from all sonnets in the db.
     */
    public List<Sonnet> getTwoRandomSonnets() {
        LOGGER.debug("Returning two random sonnets.");
        Random random = new Random();
        List<Sonnet> sonnets = getAllSonnets();
        List<Sonnet> randomSonnets = new ArrayList<>();

        while (randomSonnets.size() < NUMBER_OF_RANDOM_SONNETS) {
            Sonnet randomElement = sonnets.get(random.nextInt(sonnets.size()));
            randomSonnets.add(randomElement);
        }

        return randomSonnets;
    }

    public List<Sonnet> getAllSonnets() {
        return sonnetRepository.findAll();
    }

    public Page<Sonnet> getAllSonnetsPaged(Pageable pageRequest) {
        return sonnetRepository.findAll(pageRequest);
    }

    public List<Sonnet> getAllUserSonnets(Principal principal) {
        LOGGER.debug("Returning all sonnets for user: " + principal.getName());
        if (userDetailsService.loadUserByUsername(principal.getName()) != null) {
            return sonnetRepository.findAllByCreatedBy(principal.getName());
        } else {
            return Collections.emptyList();
        }
    }

    public Sonnet getSonnetByID(String id) {
        LOGGER.debug("Getting sonnet id: " + id);
        Sonnet sonnet = getSonnetOrError(id);
        if (sonnet != null) {
            return sonnet;
        } else {
            LOGGER.error("Sonnet with id: " + "'" + id + "'" + "does not exist.");
            return null;
        }
    }

    public List<Sonnet> getSonnetsByIds(String[] ids) {
        LOGGER.debug("Getting sonnets with ids: " + Arrays.toString(ids));
        List<Sonnet> sonnets = new ArrayList<>();

        for (String s : ids) {
            Sonnet sonnet = this.getSonnetByID(s);
            sonnets.add(sonnet);
        }

        return sonnets;
    }

    public List<Sonnet> getSonnetsByAuthorLastName(String author) {
        return sonnetRepository.findAllByLastName(author);
    }

    public List<Sonnet> getSonnetsByAddedBy(String createdBy) {
        return sonnetRepository.findAllByCreatedBy(createdBy);
    }

    public List<Sonnet> getSonnetsByAddedByAndDate(String addedBy, String after, String before) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parsedAfter = sdf.parse(after);
            Date parsedBefore = sdf.parse(before);

            return sonnetRepository.findAllByCreatedByAndConfirmedAtBetween(
                    addedBy, parsedAfter, parsedBefore);

        } catch (ParseException e) {
            LOGGER.error(e);
            return Collections.emptyList();
        }

    }

    public ResponseEntity<Void> deleteSonnetById(String id) {
        LOGGER.debug("Deleting sonnet: " + id);
        Sonnet sonnet = getSonnetOrError(id);
        if (sonnet != null) {
            sonnetRepository.delete(sonnet);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Sonnet does not exist with id: " + id);

            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

    }

    public ResponseEntity<Void> deleteUserSonnetById(String id, Principal principal) {
        LOGGER.debug("Deleting user sonnet: " + id);
        Sonnet sonnet = getSonnetOrError(id);
        if (sonnet != null && Objects.equals(sonnet.getCreatedBy(), principal.getName())) {
            sonnetRepository.delete(sonnet);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Either sonnet does not exist, or user is not authorized to delete it.");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public ResponseEntity<Void> confirmSonnet(String id, Principal principal) {
        LOGGER.debug("Confirming sonnet: " + id);
        Sonnet sonnet = getSonnetOrError(id);

        if (sonnet != null) {
            sonnet.setConfirmed(true);
            sonnet.setConfirmedBy(principal.getName());
            sonnet.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
            sonnetRepository.saveAndFlush(sonnet);
            LOGGER.debug("Sonnet confirmed!");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Sonnet not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> rejectSonnet(RejectDto rejectDto) {
        LOGGER.debug("Rejecting sonnet: " + rejectDto.getId());
        Sonnet sonnet = getSonnetOrError(rejectDto.getId());

        if (sonnet != null) {
            sonnet.setConfirmed(false);
            sonnet.setPendingRevision(true);
            sonnetRepository.saveAndFlush(sonnet);

            // Send message rejection message to user who created sonnet.
            MessageDto messageDto = new MessageDto();
            messageDto.setUserFrom("Administrator");
            messageDto.setUserTo(sonnet.getCreatedBy());
            messageDto.setSubject("One of your sonnets has been rejected.");
            messageDto.setContent(rejectDto.getRejectMessage());
            messageService.sendAdminMessage(messageDto);

            LOGGER.debug("Sonnet rejected!");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Sonnet not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Sonnet getSonnetToConfirm(Principal principal) {
        LOGGER.debug("Returning first unconfirmed sonnet in pile not submitted by: " + principal.getName());
        List<Sonnet> sonnets =
                sonnetRepository.findAllByConfirmedAndPendingRevision(false, false);
        for (Sonnet s : sonnets) {
            if (!Objects.equals(s.getCreatedBy(), principal.getName())) {
                return s;
            }
        }
        return null;
    }

    private Sonnet getSonnetOrError(String id) {
        LOGGER.debug("Attempting to retrieve sonnet with id: " + id);

        Long parseId;
        Optional<Sonnet> sonnet;
        try {
            parseId = Long.parseLong(id);
            sonnet = sonnetRepository.findById(parseId);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return sonnet.orElse(null);
    }
}
