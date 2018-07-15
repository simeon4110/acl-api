package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.corpera.CorperaDto;
import com.sonnets.sonnet.persistence.models.Corpera;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.persistence.repositories.CorperaRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD for corpera.
 *
 * @author Josh Harkema
 */
@Service
public class CorperaService {
    private static final Logger LOGGER = Logger.getLogger(CorperaRepository.class);
    private final CorperaRepository corperaRepository;
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public CorperaService(CorperaRepository corperaRepository, SonnetDetailsService sonnetDetailsService) {
        this.corperaRepository = corperaRepository;
        this.sonnetDetailsService = sonnetDetailsService;
    }

    public ResponseEntity<Void> createCorpera(CorperaDto corperaDto) {
        LOGGER.debug("Creating corpera: " + corperaDto);
        Corpera newCorpera = new Corpera();
        newCorpera.setUserId(corperaDto.getUserId());
        newCorpera.setName(corperaDto.getName());
        newCorpera.setDescription(corperaDto.getDescription());

        corperaRepository.saveAndFlush(newCorpera);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Page<Sonnet> getCorperaSonnetsPaged(String corperaId, Pageable pageable) {
        LOGGER.debug("Getting paged sonnets for corperaId" + corperaId);
        Optional<Corpera> corpera = corperaRepository.findById(Long.parseLong(corperaId));

        if (corpera.isPresent()) {
            Corpera corperaPresent = corpera.get();
            List<Sonnet> sonnetList = corperaPresent.getSonnets();

            return new PageImpl<>(sonnetList, pageable, sonnetList.size());
        } else {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    public List<Sonnet> getCorperaSonnets(String corperaId) {
        LOGGER.debug("Getting all sonnets from corperaId: " + corperaId);
        Optional<Corpera> corpera = corperaRepository.findById(Long.parseLong(corperaId));

        if (corpera.isPresent()) {
            return corpera.get().getSonnets();
        } else {
            return Collections.emptyList();
        }
    }

    public ResponseEntity<Void> addSonnets(String corperaId, String[] sonnetIds) {
        LOGGER.debug("\nAdding to coperaId: " + corperaId + "\nlist of sonnetIds: " + Arrays.toString(sonnetIds));
        Optional<Corpera> corpera = corperaRepository.findById(Long.parseLong(corperaId));

        if (corpera.isPresent()) {
            Corpera coperaPresent = corpera.get();
            for (String s : sonnetIds) {
                Sonnet sonnet = sonnetDetailsService.getSonnetByID(s);
                if (sonnet != null) {
                    coperaPresent.addSonnet(sonnet);
                }
            }
            corperaRepository.saveAndFlush(coperaPresent);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public ResponseEntity<Void> removeSonnets(String corperaId, String[] sonnetIds) {
        LOGGER.debug("\nRemoving sonnetId: " + Arrays.toString(sonnetIds) + "\nFrom corperaId: " + corperaId);
        Optional<Corpera> corpera = corperaRepository.findById(Long.parseLong(corperaId));

        if (corpera.isPresent()) {
            Corpera corperaPresent = corpera.get();
            for (String s : sonnetIds) {
                Sonnet sonnet = sonnetDetailsService.getSonnetByID(s);
                if (sonnet != null && corperaPresent.getSonnets().contains(sonnet)) {
                    corperaPresent.removeSonnet(sonnet);
                }
            }
            corperaRepository.saveAndFlush(corperaPresent);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public ResponseEntity<Void> modify(String corperaId, String name, String description) {
        LOGGER.debug("\nChanging name of " + corperaId + " to " + name);
        Optional<Corpera> corpera = corperaRepository.findById(Long.parseLong(corperaId));

        if (corpera.isPresent()) {
            Corpera corperaPresent = corpera.get();
            corperaPresent.setName(name);
            corperaPresent.setDescription(description);
            corperaRepository.saveAndFlush(corperaPresent);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
