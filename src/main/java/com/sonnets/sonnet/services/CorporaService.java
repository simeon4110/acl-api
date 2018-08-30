package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.CorporaDto;
import com.sonnets.sonnet.persistence.dtos.CorporaItemsDto;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.repositories.CorporaRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrNull;
import com.sonnets.sonnet.tools.ItemKeyValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

/**
 * Handles CRUD for corpera.
 *
 * @author Josh Harkema
 */
@Service
public class CorporaService {
    private static final Logger LOGGER = Logger.getLogger(CorporaRepository.class);
    private final CorporaRepository corporaRepository;
    private final GetObjectOrNull getObjectOrNull;

    @Autowired
    public CorporaService(CorporaRepository corporaRepository, GetObjectOrNull getObjectOrNull) {
        this.corporaRepository = corporaRepository;
        this.getObjectOrNull = getObjectOrNull;
    }

    public ResponseEntity<Void> createCorpora(CorporaDto corporaDto) {
        LOGGER.debug("Creating corpera: " + corporaDto);
        Corpora newCorpora = new Corpora();
        newCorpora.setName(corporaDto.getName());
        newCorpora.setDescription(corporaDto.getDescription());

        corporaRepository.saveAndFlush(newCorpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Page getCorporaItemsPaged(String corporaId, Pageable pageable) {
        LOGGER.debug("Getting paged items for corperaId" + corporaId);
        Corpora corpora = getCorporaOrNull(corporaId);

        if (corpora != null) {
            List<Item> itemList = new ArrayList<>(corpora.getItems());
            return new PageImpl<>(itemList, pageable, itemList.size());
        }
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    public Set<Item> getCorporaItems(String corporaId) {
        LOGGER.debug("Getting all items from corperaId: " + corporaId);
        Corpora corpora = getCorporaOrNull(corporaId);

        if (corpora != null) {
            return corpora.getItems();
        }
        return null;
    }

    public ResponseEntity<Void> addItems(CorporaItemsDto dto) {
        LOGGER.debug("Adding items: " + dto.getIds());
        Corpora corpora = getCorporaOrNull(dto.getId().toString());
        if (corpora != null) {
            Set<Item> items = corpora.getItems();
            for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
                switch (pair.getKey()) {
                    case "POEM":
                        Poem poem = getObjectOrNull.poem(pair.getValue());
                        if (poem != null) {
                            items.add(poem);
                        }
                        break;
                    case "BOOK":
                        Book book = getObjectOrNull.book(pair.getValue());
                        if (book != null) {
                            items.add(book);
                        }
                        break;
                    case "SECT":
                        Section section = getObjectOrNull.section(pair.getValue());
                        if (section != null) {
                            items.add(section);
                        }
                        break;
                }
            }
            corpora.setItems(items);
            corporaRepository.saveAndFlush(corpora);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> removeItems(CorporaItemsDto dto) {
        LOGGER.debug("Removing items: " + dto.getIds());
        Corpora corpora = getCorporaOrNull(dto.getId().toString());
        if (corpora != null) {
            Set<Item> items = corpora.getItems();
            for (ItemKeyValuePair<String, String> pair : dto.getIds()) {
                switch (pair.getKey()) {
                    case "POEM":
                        Poem poem = getObjectOrNull.poem(pair.getValue());
                        if (poem != null) {
                            items.remove(poem);
                        }
                        break;
                    case "BOOK":
                        Book book = getObjectOrNull.book(pair.getValue());
                        if (book != null) {
                            items.remove(book);
                        }
                        break;
                    case "SECT":
                        Section section = getObjectOrNull.section(pair.getValue());
                        if (section != null) {
                            items.remove(section);
                        }
                        break;
                }
            }
            corpora.setItems(items);
            corporaRepository.saveAndFlush(corpora);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> modify(String corporaId, String name, String description) {
        LOGGER.debug("Changing name of " + corporaId + " to " + name);
        Corpora corpora = getCorporaOrNull(corporaId);

        if (corpora != null) {
            corpora.setName(name);
            corpora.setDescription(description);
            corporaRepository.saveAndFlush(corpora);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> delete(String corperaId) {
        LOGGER.debug("\nDeleting corpera: " + corperaId);
        Corpora corpora = getCorporaOrNull(corperaId);

        if (corpora != null) {
            corporaRepository.delete(corpora);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List getUserCorpora(Principal principal) {
        LOGGER.debug("Returning corpera for user: " + principal.getName());

        return corporaRepository.findAllByCreatedBy(principal.getName());
    }

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
