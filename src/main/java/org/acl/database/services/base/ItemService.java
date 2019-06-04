package org.acl.database.services.base;

import org.acl.database.persistence.models.base.Item;
import org.acl.database.persistence.repositories.BookRepository;
import org.acl.database.persistence.repositories.SectionRepositoryBase;
import org.acl.database.persistence.repositories.ShortStoryRepository;
import org.acl.database.persistence.repositories.poem.PoemRepository;
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
    private final BookRepository bookRepository;
    private final PoemRepository poemRepository;
    private final SectionRepositoryBase sectionRepositoryBase;
    private final ShortStoryRepository shortStoryRepository;

    @Autowired
    public ItemService(BookRepository bookRepository, PoemRepository poemRepository,
                       SectionRepositoryBase sectionRepositoryBase, ShortStoryRepository shortStoryRepository) {
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
