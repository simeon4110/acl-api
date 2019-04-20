package com.sonnets.sonnet.services.base;

import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.repositories.BookRepository;
import com.sonnets.sonnet.persistence.repositories.ItemRepository;
import com.sonnets.sonnet.persistence.repositories.SectionRepositoryBase;
import com.sonnets.sonnet.persistence.repositories.ShortStoryRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is for handling requests that span multiple item types.
 *
 * @author Josh Harkema
 */
@Service
public class ItemService {
    private static final Logger LOGGER = Logger.getLogger(ItemService.class);
    private final ItemRepository itemRepository;
    private final BookRepository bookRepository;
    private final PoemRepository poemRepository;
    private final SectionRepositoryBase sectionRepositoryBase;
    private final ShortStoryRepository shortStoryRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, BookRepository bookRepository, PoemRepository poemRepository,
                       SectionRepositoryBase sectionRepositoryBase, ShortStoryRepository shortStoryRepository) {
        this.itemRepository = itemRepository;
        this.bookRepository = bookRepository;
        this.poemRepository = poemRepository;
        this.sectionRepositoryBase = sectionRepositoryBase;
        this.shortStoryRepository = shortStoryRepository;
    }

    /**
     * @param principal the principal of the user making the request.
     * @return the basic details of all items added by the user making the request.
     */
    public List<Item> getAllUserItems(Principal principal) {
        LOGGER.debug("Returning all items created by user " + principal.getName());
        ArrayList<Item> out = new ArrayList<>();

        out.addAll(this.bookRepository.findAllByCreatedBy(principal.getName()).orElse(Collections.emptyList()));
        out.addAll(this.poemRepository.findAllByCreatedBy(principal.getName()).orElse(Collections.emptyList()));
        out.addAll(this.sectionRepositoryBase.findAllByCreatedBy(principal.getName())
                .orElse(Collections.emptyList()));
        out.addAll(this.shortStoryRepository.findAllByCreatedBy(principal.getName())
                .orElse(Collections.emptyList()));
        return out;
    }
}
