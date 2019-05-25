package com.sonnets.sonnet.services.web;

import com.sonnets.sonnet.helpers.ItemKeyValuePair;
import com.sonnets.sonnet.persistence.dtos.web.CorporaBasicOutDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaItemsDto;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.repositories.BookRepository;
import com.sonnets.sonnet.persistence.repositories.CorporaRepository;
import com.sonnets.sonnet.persistence.repositories.OtherRepository;
import com.sonnets.sonnet.persistence.repositories.SectionRepositoryBase;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.services.exceptions.CorporaNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

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
    private final BookRepository bookRepository;
    private final PoemRepository poemRepository;
    private final SectionRepositoryBase sectionRepositoryBase;
    private final OtherRepository otherRepository;

    public CorporaService(CorporaRepository corporaRepository, BookRepository bookRepository,
                          PoemRepository poemRepository, SectionRepositoryBase sectionRepositoryBase,
                          OtherRepository otherRepository) {
        this.corporaRepository = corporaRepository;
        this.bookRepository = bookRepository;
        this.poemRepository = poemRepository;
        this.sectionRepositoryBase = sectionRepositoryBase;
        this.otherRepository = otherRepository;
    }

    /**
     * Parses ItemKeyValuePairs into a set of Items.
     *
     * @param dto containing the KeyValuePairs.
     * @return a set of items.
     */
    private Set<Item> parseItems(final CorporaItemsDto dto) {
        Set<Item> items = new HashSet<>();
        for (ItemKeyValuePair<String, Long> pair : dto.getIds()) {
            LOGGER.debug("Key value pair: " + pair.toString());
            switch (pair.getType()) {
                case TypeConstants.BOOK:
                    items.add(bookRepository.getOne(pair.getId()));
                    break;
                case TypeConstants.POEM:
                    items.add(poemRepository.getOne(pair.getId()));
                    break;
                case TypeConstants.SECTION:
                    items.add(sectionRepositoryBase.getOne(pair.getId()));
                    break;
                case TypeConstants.OTHER:
                    items.add(otherRepository.getOne(pair.getId()));
                    break;
                default:
                    break;
            }
        }
        return items;
    }

    /**
     * Add a new corpora.
     *
     * @param dto with the details for the new corpora.
     * @return OK if good.
     */
    public ResponseEntity<Void> add(final CorporaDto dto) {
        LOGGER.debug("Creating corpora: " + dto);
        Corpora newCorpora = new Corpora();
        newCorpora.setName(dto.getName());
        newCorpora.setDescription(dto.getDescription());
        corporaRepository.save(newCorpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Add a list of items to a corpora.
     *
     * @param dto       with the items.
     * @param principal of the user making the request.
     * @return OK if good, UNAUTHORIZED if user does not own corpora.
     */
    public ResponseEntity<Void> addItems(final CorporaItemsDto dto, Principal principal) {
        LOGGER.debug("Adding items to corpora: " + dto.toString());
        Corpora corpora = corporaRepository.findById(dto.getId()).orElseThrow(CorporaNotFoundException::new);

        // Verify user owns corpora.
        if (!corpora.getCreatedBy().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        corpora.getItems().addAll(parseItems(dto));
        corpora.setTotalItems(corpora.getItems().size());
        corporaRepository.save(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Remove a list of items from a corpora.
     *
     * @param dto       with the items.
     * @param principal of the user making the request.
     * @return OK if good, UNAUTHORIZED if user does not own corpora.
     */
    public ResponseEntity<Void> removeItems(final CorporaItemsDto dto, Principal principal) {
        LOGGER.debug("Removing items from corpora: " + dto.toString());
        Corpora corpora = corporaRepository.findById(dto.getId()).orElseThrow(CorporaNotFoundException::new);

        // Verify user owns corpora.
        if (!corpora.getCreatedBy().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        corpora.getItems().removeAll(parseItems(dto));
        corpora.setTotalItems(corpora.getItems().size());
        corporaRepository.save(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get a corpora by id.
     *
     * @param id of the corpora.
     * @return the corpora, throws CorporaNotFoundException if corpora does not exist.
     */
    public Corpora get(final Long id) {
        LOGGER.debug("Returning corpora: " + id.toString());
        return corporaRepository.findById(id).orElseThrow(CorporaNotFoundException::new);
    }

    /**
     * Get a corpora by id.
     *
     * @param id of the corpora.
     * @return the corpora's basic details, throws CorporaNotFoundException if corpora does not exist.
     * Does not return any of the corpora's items, saving on SQL time and JSON out size.
     */
    public CorporaBasicOutDto getBasic(final Long id) {
        LOGGER.debug("Returning corpora (basic): " + id.toString());
        return corporaRepository.getByIdBasic(id).orElseThrow(CorporaNotFoundException::new);
    }

    /**
     * Modify a corpora's details.
     *
     * @param dto       with the new details.
     * @param principal of the user making the request.
     * @return OK if good, UNAUTHORIZED if user does not own corpora.
     */
    public ResponseEntity<Void> modify(final CorporaDto dto, Principal principal) {
        LOGGER.debug("Modifying corpora: " + dto.toString());
        Corpora corpora = corporaRepository.findById(dto.getId()).orElseThrow(CorporaNotFoundException::new);

        // Verify user owns corpora.
        if (!corpora.getCreatedBy().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        corpora.setName(dto.getName());
        corpora.setDescription(dto.getDescription());
        corporaRepository.save(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a corpora.
     *
     * @param id        of the corpora to delete.
     * @param principal of the user making the request.
     * @return OK if good, UNAUTHORIZED if user does not own corpora.
     */
    public ResponseEntity<Void> delete(final Long id, Principal principal) {
        LOGGER.debug("Deleting corpora: " + id);
        Corpora corpora = corporaRepository.findById(id).orElseThrow(CorporaNotFoundException::new);

        // Verify user owns corpora.
        if (!corpora.getCreatedBy().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        corporaRepository.delete(corpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
