package com.sonnets.sonnet.models;

import com.sonnets.sonnet.repositories.SonnetRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        toAddSonnet.setPublicationYear(newSonnet.getPublicationYear());

        // Push the lines into a list array.
        List<String> text = new ArrayList<>();
        text.add(newSonnet.getLine1());
        text.add(newSonnet.getLine2());
        text.add(newSonnet.getLine3());
        text.add(newSonnet.getLine4());
        text.add(newSonnet.getLine5());
        text.add(newSonnet.getLine6());
        text.add(newSonnet.getLine7());
        text.add(newSonnet.getLine8());
        text.add(newSonnet.getLine9());
        text.add(newSonnet.getLine10());
        text.add(newSonnet.getLine11());
        text.add(newSonnet.getLine12());
        text.add(newSonnet.getLine13());
        text.add(newSonnet.getLine14());

        toAddSonnet.setText(text);

        sonnetRepository.save(toAddSonnet);
        return toAddSonnet;
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
