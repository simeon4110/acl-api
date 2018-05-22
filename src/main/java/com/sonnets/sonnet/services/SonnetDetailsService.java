package com.sonnets.sonnet.services;

import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDto;
import com.sonnets.sonnet.repositories.SonnetRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Basic service to interface with SonnetRepository. More search and analytics will be added here.
 *
 * @author Josh Harkema
 */
@Service
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
    @SuppressWarnings("UnusedReturnValue")
    public Sonnet addNewSonnet(SonnetDto newSonnet) {
        Sonnet toAddSonnet = new Sonnet();

        if (logger.isDebugEnabled()) {
            logger.debug("Adding sonnet: " + "'" + newSonnet + "'");
        }

        toAddSonnet.setFirstName(newSonnet.getFirstName());
        toAddSonnet.setLastName(newSonnet.getLastName());
        toAddSonnet.setTitle(newSonnet.getTitle());
        toAddSonnet.setPublicationStmt(newSonnet.getPublicationStmt());
        toAddSonnet.setSourceDesc(newSonnet.getSourceDesc());

        String[] text = newSonnet.getText().split("\\r?\\n");
        List<String> strings = new ArrayList<>();
        Collections.addAll(strings, text);

        toAddSonnet.setText(strings);

        sonnetRepository.save(toAddSonnet);
        return toAddSonnet;
    }

    public Sonnet updateSonnet(SonnetDto newSonnet) {
        Sonnet sonnet;

        if (sonnetRepository.findById(newSonnet.getId()).isPresent()) {
            sonnet = sonnetRepository.findById(newSonnet.getId()).get();
        } else {
            logger.error("Sonnet does not exist with id: " + newSonnet.getId());
            return null;
        }

        sonnet.setFirstName(newSonnet.getFirstName());
        sonnet.setLastName(newSonnet.getLastName());
        sonnet.setTitle(newSonnet.getTitle());
        sonnet.setPublicationStmt(newSonnet.getPublicationStmt());
        sonnet.setSourceDesc(newSonnet.getSourceDesc());

        String[] text = newSonnet.getText().split("\\r?\\n");
        List<String> strings = new ArrayList<>();
        Collections.addAll(strings, text);

        sonnet.setText(strings);

        sonnetRepository.save(sonnet);
        return sonnet;
    }

    public void deleteSonnet(String id) {
        Optional<Sonnet> sonnet = sonnetRepository.findById(Long.parseLong(id));
        if (sonnet.isPresent()) {
            sonnetRepository.delete(sonnet.get());
        } else {
            logger.error("Sonnet does not exist with id: " + id);
        }
    }

    public List<Sonnet> getAllSonnets() {
        return sonnetRepository.findAll();
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

}
