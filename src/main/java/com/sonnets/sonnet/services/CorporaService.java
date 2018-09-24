package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.ItemOutDto;
import com.sonnets.sonnet.persistence.dtos.base.ItemOutSimpleDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaItemsDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.repositories.corpora.CorporaRepository;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.tools.ItemKeyValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
     * Get a single corpora by db id. Runs async in anon executors.
     *
     * @param corporaId the id of the corpora to get.
     * @return the corpora or null if not found.
     */
    public Corpora getSingle(String corporaId) {
        LOGGER.debug("Getting corpora by id:" + corporaId);
        return corporaRepository.getCorpora(Long.valueOf(corporaId)).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Add a single item to a corpora.
     *
     * @param type      the item's type ('BOOK', 'POEM', 'SECT')
     * @param corporaId the db id of the corpora to add the item to.
     * @param itemId    the db id of the item to add.
     * @return 200 if successful.
     */
    public ResponseEntity<Void> addSingleItem(String type, String corporaId, String itemId) {
        LOGGER.debug("Adding single item: " + type + ' ' + corporaId + ' ' + itemId);
        CompletableFuture.runAsync(() ->
                corporaRepository.addCorporaItem(Long.valueOf(corporaId), Long.valueOf(itemId), type));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Add items to a corpora. Runs async in anon executors.
     *
     * @param dto the dto with the items to add.
     * @return OK if the items are added.
     */
    public ResponseEntity<Void> addItems(CorporaItemsDto dto) {
        LOGGER.debug("Adding items: " + dto.getIds());
        for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
            CompletableFuture.runAsync(() ->
                    corporaRepository.addCorporaItem(dto.getId(), Long.valueOf(pair.getValue()), pair.getKey()));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Remove items from a corpora. Runs async in anon executors.
     *
     * @param dto the dto with the items to remove.
     * @return OK if the items are removed.
     */
    public ResponseEntity<Void> removeItems(CorporaItemsDto dto) {
        LOGGER.debug("Removing items: " + dto.getIds());
        for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
            CompletableFuture.runAsync(() ->
                    corporaRepository.removeCorporaItem(dto.getId(), Long.valueOf(pair.getValue()), pair.getKey()));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify an existing corpora.
     *
     * @param corporaId   the id of the corpora to modify.
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
     * Get all corpora by a given user.
     *
     * @param principal the principal of the request.
     * @return a list of corpora (empty if nothing is found.)
     */
    public List getUserCorpora(Principal principal) {
        LOGGER.debug("Returning corpera for user: " + principal.getName());
        return corporaRepository.getCorporaUser(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Gets a set of all of a corpora's items. The getCorporaItems method runs async.
     *
     * @param id the db id of the corpora to get.
     * @return a set of all the items in the corpora.
     */
    public Set<ItemOutDto> getCorporaItems(String id) {
        LOGGER.debug("Getting corpora items: " + id);
        CompletableFuture<Optional<HashSet<ItemOutDto>>> future = corporaRepository.getCorporaItems(Long.valueOf(id));
        return future.join().orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Gets just the basics of a corpora's items. The getCorporaItemsSimple method runs async.
     *
     * @param id the db id of the corpora's items to get.
     * @return a set of the basic details for all items in a corpora.
     */
    public Set<ItemOutSimpleDto> getCorporaItemsSimple(String id) {
        LOGGER.debug("Getting corpora items simple: " + id);
        CompletableFuture<Optional<HashSet<ItemOutSimpleDto>>> future =
                corporaRepository.getCorporaItemsSimple(Long.valueOf(id));
        return future.join().orElseThrow(ItemNotFoundException::new);
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
