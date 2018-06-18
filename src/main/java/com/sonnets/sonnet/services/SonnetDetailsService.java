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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private static final Logger LOGGER = Logger.getLogger(SonnetDetailsService.class);

    @Autowired
    public SonnetDetailsService(SonnetRepository sonnetRepository, SearchService searchService) {
        this.sonnetRepository = sonnetRepository;
        this.searchService = searchService;
    }

    /**
     * Add a new Sonnet object from a @Valid SonnetDto.
     *
     * @param newSonnet a valid SonnetDto object.
     * @return the Sonnet object created.
     */
    public Sonnet addNewSonnet(SonnetDto newSonnet) {
        LOGGER.debug("Adding sonnet: " + "'" + newSonnet + "'");
        try {
            searchService.similarExists(newSonnet);
            Sonnet toAddSonnet = new Sonnet(newSonnet);
            sonnetRepository.saveAndFlush(toAddSonnet);

            return toAddSonnet;
        } catch (SonnetAlreadyExistsException e) {
            LOGGER.error(e);

            return null;
        }
    }

    /**
     * Updates an existing sonnet.
     *
     * @param updateSonnet the SonnetDto of the new data.
     * @return the sonnet with the updated data.
     */
    public Sonnet updateSonnet(SonnetDto updateSonnet) {
        Optional<Sonnet> sonnetToEdit = sonnetRepository.findById(updateSonnet.getId());
        Sonnet sonnet;
        if (sonnetToEdit.isPresent()) {
            sonnet = sonnetToEdit.get();
        } else {
            LOGGER.error("Sonnet does not exist with id: " + updateSonnet.getId());

            return null;
        }

        sonnet = sonnet.update(updateSonnet);
        sonnetRepository.saveAndFlush(sonnet);

        return sonnet;
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
            LOGGER.error("Sonnet with id: " + "'" + id + "'" + "does not exist.");
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

    public String deleteSonnetById(String id) {
        Long idNum;
        try {
            idNum = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid number format: " + id);

            return "redirect:/admin/sonnets/all?invalidId";
        }

        Optional<Sonnet> sonnetOp = sonnetRepository.findById(idNum);
        Sonnet sonnet;
        if (sonnetOp.isPresent()) {
            sonnet = sonnetOp.get();
            sonnetRepository.delete(sonnet);

            return "redirect:/admin/sonnets/all?success";
        } else {
            LOGGER.error("Sonnet does not exist with id: " + idNum);

            return "redirect:/admin/sonnets/all?doesNotExist";
        }

    }

    public Sonnet getSonnetByTitleAndLastName(String title, String lastName) {
        return sonnetRepository.findByTitleAndLastName(title, lastName);
    }

}
