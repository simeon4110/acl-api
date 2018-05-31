package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.exceptions.SonnetAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.persistence.repositories.SonnetRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Basic service to interface with SonnetRepository. More search and analytics will be added here.
 *
 * @author Josh Harkema
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class SonnetDetailsService {
    private final SonnetRepository sonnetRepository;
    private static final Logger logger = Logger.getLogger(SonnetDetailsService.class);

    @Autowired
    public SonnetDetailsService(SonnetRepository sonnetRepository) {
        this.sonnetRepository = sonnetRepository;
    }

    /**
     * Add a new Sonnet object from a @Valid SonnetDto.
     *
     * @param newSonnet a valid SonnetDto object.
     * @return the Sonnet object created.
     */
    public Sonnet addNewSonnet(SonnetDto newSonnet) {
        logger.debug("Adding sonnet: " + "'" + newSonnet + "'");
        try {
            checkIfSonnetExists(newSonnet);
            Sonnet toAddSonnet = new Sonnet(newSonnet);
            sonnetRepository.save(toAddSonnet);

            return toAddSonnet;
        } catch (SonnetAlreadyExistsException e) {
            logger.error(e);

            return null;
        }

    }

    /**
     * Updates an existing sonnet.
     *
     * @param newSonnet the SonnetDto of the new data.
     * @return the sonnet with the updated data.
     */
    public Sonnet updateSonnet(SonnetDto newSonnet) {
        Optional<Sonnet> sonnetToEdit = sonnetRepository.findById(newSonnet.getId());
        Sonnet sonnet;
        if (sonnetToEdit.isPresent()) {
            sonnet = sonnetToEdit.get();
        } else {
            logger.error("Sonnet does not exist with id: " + newSonnet.getId());

            return null;
        }

        sonnet = sonnet.update(newSonnet);
        sonnetRepository.save(sonnet);

        return sonnet;
    }

    /**
     * For updating an actual sonnet object.
     *
     * @param sonnet the sonnet to update.
     */
    public void updateSonnet(Sonnet sonnet) {
        sonnetRepository.save(sonnet);
    }

    public List<Sonnet> getAllSonnets() {
        return sonnetRepository.findAll();
    }

    public Page<Sonnet> getAllSonnetsPaged(Pageable pageRequest) {
        return sonnetRepository.findAll(pageRequest);
    }

    public Sonnet getSonnetByID(String id) {
        Optional<Sonnet> sonnet = sonnetRepository.findById(Long.parseLong(id));
        if (sonnet.isPresent()) {
            return sonnet.get();
        } else {
            logger.error("Sonnet with id: " + "'" + id + "'" + "does not exist.");
            return null;
        }
    }

    public List<Sonnet> getSonnetsByIds(String[] ids) {
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

    public List<Sonnet> getSonnetsByAuthorFirstName(String author) {
        return sonnetRepository.findAllByFirstName(author);
    }

    public List<Sonnet> getSonnetsByAddedBy(String addedBy) {
        return sonnetRepository.findAllByAddedBy(addedBy);
    }

    /**
     * Delete a specific sonnet by its id.
     *
     * @param id the id as a String.
     * @return success / error redirect.
     */
    public String deleteSonnetById(String id) {
        Long idNum;
        try {
            idNum = Long.parseLong(id);
        } catch (NumberFormatException e) {
            logger.error("Invalid number format: " + id);

            return "redirect:/admin/sonnets/all?invalidId";
        }

        Optional<Sonnet> sonnetOp = sonnetRepository.findById(idNum);
        Sonnet sonnet;
        if (sonnetOp.isPresent()) {
            sonnet = sonnetOp.get();
            sonnetRepository.delete(sonnet);

            return "redirect:/admin/sonnets/all?success";
        } else {
            logger.error("Sonnet does not exist with id: " + idNum);

            return "redirect:/admin/sonnets/all?doesNotExist";
        }

    }

    public Sonnet getSonnetByTitleAndLastName(String title, String lastName) {
        return sonnetRepository.findByTitleAndLastName(title, lastName);
    }

    /**
     * Checks if a sonnet by that title and author exists and throws an exception if it does.
     *
     * @param sonnetDto the sonnet to check if it exists.
     */
    private void checkIfSonnetExists(SonnetDto sonnetDto) {
        if (sonnetRepository.findByTitleAndLastName(sonnetDto.getTitle(), sonnetDto.getLastName()) != null) {
            throw new SonnetAlreadyExistsException("Sonnet with title: " + sonnetDto.getTitle() + " by author " +
                    sonnetDto.getLastName() + " already exists.");
        }
    }

}
