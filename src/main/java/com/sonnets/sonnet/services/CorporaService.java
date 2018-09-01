package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaItemsDto;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.repositories.CorporaRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullException;
import com.sonnets.sonnet.tools.ItemKeyValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Handles CRUD for Corpora.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SqlResolve")
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CorporaService {
    private static final Logger LOGGER = Logger.getLogger(CorporaService.class);
    private final CorporaRepository corporaRepository;
    private final GetObjectOrThrowNullException getObjectOrNull;
    private final EntityManager em;

    @Autowired
    public CorporaService(CorporaRepository corporaRepository, GetObjectOrThrowNullException getObjectOrNull, EntityManager em) {
        this.corporaRepository = corporaRepository;
        this.getObjectOrNull = getObjectOrNull;
        this.em = em;
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
        corporaRepository.saveAndFlush(newCorpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get a single corpora by db id.
     *
     * @param corporaId the id of the corpora to get.
     * @return the corpora or null if not found.
     */
    public Corpora getSingle(String corporaId) {
        LOGGER.debug("Getting corpora by id:" + corporaId);
        return getCorporaOrNull(corporaId);
    }

    /**
     * Add items to a corpora.
     *
     * @param dto the dto with the items to add.
     * @return OK if the items are added.
     */
    public ResponseEntity<Void> addItems(CorporaItemsDto dto) {
        LOGGER.debug("Adding items: " + dto.getIds());
        Corpora corpora = getCorporaOrNull(dto.getId().toString());
        assert corpora != null;
        Set<Item> items = corpora.getItems();
        for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
            switch (pair.getKey()) {
                case "POETRY":
                    Poem poem = getObjectOrNull.poem(pair.getValue());
                    items.add(poem);
                    break;
                case "PROSE":
                    Book book = getObjectOrNull.book(pair.getValue());
                    items.add(book);
                    break;
                case "SECTION":
                    Section section = getObjectOrNull.section(pair.getValue());
                    items.add(section);
                    break;
                default:
                    break;
            }
            corpora.setItems(items);
        }
        corporaRepository.saveAndFlush(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Remove items from a corpora. DO NOT FUCK WITH THE QUERIES.
     *
     * @param dto the dto with the items to remove.
     * @return OK if the items are removed.
     */
    public ResponseEntity<Void> removeItems(CorporaItemsDto dto) {
        LOGGER.debug("Removing items: " + dto.getIds());
        Corpora corpora = getCorporaOrNull(dto.getId().toString());
        assert corpora != null;
        for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
            switch (pair.getKey()) {
                case "POETRY":
                    Poem poem = getObjectOrNull.poem(pair.getValue());
                    em.createNativeQuery("DELETE FROM `corpora_items` WHERE  " +
                            "`corpora_id` = :corporaId AND " +
                            "`item_type` LIKE  'POEM' ESCAPE '#' AND " +
                            "`item_id` = :itemId")
                            .setParameter("corporaId", corpora.getId().toString())
                            .setParameter("itemId", poem.getId().toString())
                            .executeUpdate();
                    break;
                case "PROSE":
                    Book book = getObjectOrNull.book(pair.getValue());
                    em.createNativeQuery("DELETE FROM `corpora_items` WHERE  " +
                            "`corpora_id` = :corporaId AND " +
                            "`item_type` LIKE  'BOOK' ESCAPE '#' AND " +
                            "`item_id` = :itemId")
                            .setParameter("corporaId", corpora.getId().toString())
                            .setParameter("itemId", book.getId().toString())
                            .executeUpdate();
                    break;
                case "SECTION":
                    Section section = getObjectOrNull.section(pair.getValue());
                    em.createNativeQuery("DELETE FROM `corpora_items` WHERE  " +
                            "`corpora_id` = :corporaId AND " +
                            "`item_type` LIKE  'SECT' ESCAPE '#' AND " +
                            "`item_id` = :itemId")
                            .setParameter("corporaId", corpora.getId().toString())
                            .setParameter("itemId", section.getId().toString())
                            .executeUpdate();
                    break;
                default:
                    break;
            }
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
        corporaRepository.saveAndFlush(corpora);
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
        return corporaRepository.findAllByCreatedBy(principal.getName());
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
        return corporaOptional.orElse(null);
    }
}
