package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.sonnet.ConfirmDto;
import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.exceptions.SonnetAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.Sonnet;
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
    private static final Logger LOGGER = Logger.getLogger(SonnetDetailsService.class);
    private static final int NUMBER_OF_RANDOM_SONNETS = 2;

    @Autowired
    public SonnetDetailsService(SonnetRepository sonnetRepository, SearchService searchService,
                                UserDetailsServiceImpl userDetailsService) {
        this.sonnetRepository = sonnetRepository;
        this.searchService = searchService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Add a new Sonnet object from a @Valid SonnetDto.
     *
     * @param newSonnet a valid SonnetDto object.
     * @return the Sonnet object created.
     */
    public ResponseEntity<Void> addNewSonnet(SonnetDto newSonnet) {
        LOGGER.debug("Adding sonnet: " + "'" + newSonnet + "'");
        try {
            searchService.similarExists(newSonnet);
            Sonnet toAddSonnet = new Sonnet(newSonnet);
            sonnetRepository.saveAndFlush(toAddSonnet);

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
        Optional<Sonnet> sonnetToEdit = sonnetRepository.findById(updateSonnet.getId());

        if (sonnetToEdit.isPresent()) {
            Sonnet sonnet = sonnetToEdit.get();
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
        return sonnetRepository.findAllByAddedBy(principal.getName());
    }

    public Sonnet getSonnetByID(String id) {
        Optional<Sonnet> sonnet = sonnetRepository.findById(Long.parseLong(id));
        if (sonnet.isPresent()) {
            return sonnet.get();
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

    public List<Sonnet> getSonnetsByAddedBy(String addedBy) {
        return sonnetRepository.findAllByAddedBy(addedBy);
    }

    public List<Sonnet> getSonnetsByAddedByAndDate(String addedBy, String after, String before) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parsedAfter = sdf.parse(after);
            Date parsedBefore = sdf.parse(before);

            return sonnetRepository.findAllByAddedByAndUpdatedAtBetween(addedBy, parsedAfter, parsedBefore);

        } catch (ParseException e) {
            LOGGER.error(e);
            return Collections.emptyList();
        }

    }

    public ResponseEntity<Void> deleteSonnetById(String id) {
        LOGGER.debug("Deleting sonnet: " + id);
        Long idNum;
        try {
            idNum = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid number format: " + id);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Sonnet> sonnetOp = sonnetRepository.findById(idNum);
        Sonnet sonnet;
        if (sonnetOp.isPresent()) {
            sonnet = sonnetOp.get();
            sonnetRepository.delete(sonnet);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Sonnet does not exist with id: " + idNum);

            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

    }

    public ResponseEntity<Void> confirmSonnet(ConfirmDto confirmDto, Principal principal) {
        LOGGER.debug("Confirming sonnet: " + confirmDto.toString());
        Optional<Sonnet> sonnet = sonnetRepository.findById(confirmDto.getId());
        if (sonnet.isPresent()) {
            Sonnet sonnetActual = sonnet.get();
            sonnetActual.setConfirmed(true);
            sonnetActual.setConfirmedBy(userDetailsService.loadUserObjectByUsername(principal.getName()));
            sonnetActual.setConfirmedAt(new Timestamp(System.currentTimeMillis()));

            sonnetRepository.saveAndFlush(sonnetActual);

            LOGGER.debug("Sonnet confirmed!");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.debug("Sonnet not found.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
