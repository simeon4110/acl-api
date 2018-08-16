package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.CorporaDto;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.repositories.CorporaRepository;
import com.sonnets.sonnet.persistence.repositories.ItemRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
public class CorporaService {
    private static final Logger LOGGER = Logger.getLogger(CorporaRepository.class);
    private final CorporaRepository corporaRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CorporaService(CorporaRepository corporaRepository, ItemRepository itemRepository) {
        this.corporaRepository = corporaRepository;
        this.itemRepository = itemRepository;
    }

    public ResponseEntity<Void> createCorpera(CorporaDto corporaDto) {
        LOGGER.debug("Creating corpera: " + corporaDto);
        Corpora newCorpora = new Corpora();
        newCorpora.setName(corporaDto.getName());
        newCorpora.setDescription(corporaDto.getDescription());

        corporaRepository.saveAndFlush(newCorpora);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Page<Item> getCorporaItemsPaged(String corporaId, Pageable pageable) {
        LOGGER.debug("Getting paged items for corperaId" + corporaId);
        Corpora corpora = getCorporaOrNull(corporaId);

        if (corpora != null) {
            List<Item> itemList = corpora.getItems();
            return new PageImpl<>(itemList, pageable, itemList.size());
        }
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    public List<Item> getCorporaItems(String corporaId) {
        LOGGER.debug("Getting all items from corperaId: " + corporaId);
        Corpora corpora = getCorporaOrNull(corporaId);

        if (corpora != null) {
            return corpora.getItems();
        }
        return Collections.emptyList();
    }

    public ResponseEntity<Void> addItems(String corporaId, String[] itemIds) {
        LOGGER.debug("Adding to corporaId: " + corporaId + "list of sonnetIds: " + Arrays.toString(itemIds));
        Corpora corpora = getCorporaOrNull(corporaId);

        if (corpora != null) {
            for (String s : itemIds) {
                Item item = getItemOrNull(s);
                if (item != null && !corpora.getItems().contains(item)) {
                    corpora.getItems().add(item);
                }
            }
            corporaRepository.saveAndFlush(corpora);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> removeItems(String corporaId, String[] sonnetIds) {
        LOGGER.debug("Removing itemIds: " + Arrays.toString(sonnetIds) + "From corporaId: " + corporaId);
        Corpora corpora = getCorporaOrNull(corporaId);

        if (corpora != null) {
            for (String s : sonnetIds) {
                Item item = getItemOrNull(s);
                if (item != null) {
                    corpora.getItems().remove(item);
                }
            }
            corporaRepository.saveAndFlush(corpora);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

    public List getUserCorpera(Principal principal) {
        LOGGER.debug("Returning corpera for user: " + principal.getName());

        return corporaRepository.findAllByCreatedBy(principal.getName());
    }

    private Item getItemOrNull(final String itemId) {
        long parsedId;
        try {
            parsedId = Long.parseLong(itemId);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        Optional<Item> itemOptional = itemRepository.findById(parsedId);
        return itemOptional.orElse(null);
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
