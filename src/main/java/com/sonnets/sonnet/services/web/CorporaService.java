package com.sonnets.sonnet.services.web;

import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.repositories.CorporaRepository;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Handles CRUD for Corpora. Input queries run async so the user's thread isn't blocked while waiting for the query
 * to complete.
 *
 * @author Josh Harkema
 */
@Service
public class CorporaService {
    private static final Logger LOGGER = Logger.getLogger(CorporaService.class);
    private final CorporaRepository corporaRepository;

    @Autowired
    public CorporaService(CorporaRepository corporaRepository) {
        this.corporaRepository = corporaRepository;
    }

    /**
     * Add a new corpora to the db.
     *
     * @param corporaDto the data for the new corpora.
     * @return OK if the corpora is added.
     */
    public ResponseEntity<Void> createCorpora(CorporaDto corporaDto) {
        LOGGER.debug("Creating corpera: " + corporaDto);
        Corpora newCorpora = new Corpora();
        newCorpora.setName(corporaDto.getName());
        newCorpora.setDescription(corporaDto.getDescription());
        corporaRepository.save(newCorpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify an existing corpora.
     *
     * @param corporaId   the id of the corpora to modifyUser.
     * @param name        the new/old name of the corpora.
     * @param description the new/old description of the corpora.
     * @return OK if the corpora is modified.
     */
    public ResponseEntity<Void> modify(String corporaId, String name, String description) {
        LOGGER.debug("Changing name of " + corporaId + " to " + name);
        Corpora corpora = getCorporaOrNull(corporaId);
        assert corpora != null;
        corpora.setName(name);
        corpora.setDescription(description);
        corporaRepository.save(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a corpora.
     *
     * @param corperaId the id of the corpora to delete.
     * @return OK if the corpora is deleted.
     */
    public ResponseEntity<Void> delete(String corperaId) {
        LOGGER.debug("\nDeleting corpera: " + corperaId);
        Corpora corpora = getCorporaOrNull(corperaId);
        assert corpora != null;
        corporaRepository.delete(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Helper method that returns null if a corpora is not found.
     *
     * @param corporaId the id of the corpora to get.
     * @return the corpora or null
     */
    private Corpora getCorporaOrNull(final String corporaId) {
        long parsedId;
        try {
            parsedId = Long.parseLong(corporaId);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        Optional<Corpora> corporaOptional = corporaRepository.findById(parsedId);
        return corporaOptional.orElseThrow(ItemNotFoundException::new);
    }
}
