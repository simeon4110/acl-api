package org.acl.database.services.base;

import org.acl.database.config.LuceneConfig;
import org.acl.database.persistence.dtos.base.ShortStoryDto;
import org.acl.database.persistence.dtos.base.ShortStoryOutDto;
import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.base.ShortStory;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.persistence.repositories.ShortStoryRepository;
import org.acl.database.search.SearchRepository;
import org.acl.database.security.UserDetailsServiceImpl;
import org.acl.database.services.AbstractItemService;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.tools.ParseSourceDetails;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Deals with everything related to short stories.
 *
 * @author Josh Harkema
 */
@Service
public class ShortStoryService implements AbstractItemService<ShortStory, ShortStoryDto, ShortStoryOutDto> {
    private static final Logger LOGGER = Logger.getLogger(ShortStoryService.class);
    private static final ParseSourceDetails<ShortStory, ShortStoryDto> parseSourceDetails = new ParseSourceDetails<>();
    private final ShortStoryRepository shortStoryRepository;
    private final AuthorRepository authorRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public ShortStoryService(ShortStoryRepository shortStoryRepository, AuthorRepository authorRepository,
                             UserDetailsServiceImpl userDetailsService) {
        this.shortStoryRepository = shortStoryRepository;
        this.authorRepository = authorRepository;
        this.userDetailsService = userDetailsService;
    }

    private static ShortStory createOrCopyShortStory(ShortStory shortStory, Author author, ShortStoryDto dto) {
        shortStory.setCategory(TypeConstants.SHORT_STORY);
        shortStory.setAuthor(author);
        shortStory.setTitle(dto.getTitle());
        shortStory = parseSourceDetails.parse(shortStory, dto);
        shortStory.setText(dto.getText());
        return shortStory;
    }

    private static void addNewSearchDocument(final ShortStory shortStory) {
        LOGGER.debug("Adding new ShortStory com.sonnets.sonnet.search document...");
        Document document = SearchRepository.parseCommonFields(new Document(), shortStory);
        document.add(LuceneConfig.getTextField(shortStory.getText()));
        SearchRepository.addDocument(document, TypeConstants.SHORT_STORY);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> add(ShortStoryDto dto) {
        LOGGER.debug("Adding new short story: " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        ShortStory shortStory = createOrCopyShortStory(new ShortStory(), author, dto);
        addNewSearchDocument(shortStoryRepository.save(shortStory));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id, Principal principal) {
        LOGGER.debug("Deleting short story with id (ADMIN): " + id);
        ShortStory shortStory = shortStoryRepository.findById(id).orElseThrow(ItemNotFoundException::new);

        if (principal.getName().equals(shortStory.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            shortStoryRepository.delete(shortStory);
            SearchRepository.deleteDocument(String.valueOf(id), TypeConstants.SHORT_STORY);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    @Transactional(readOnly = true)
    public ShortStory getById(Long id) {
        LOGGER.debug("Returning short story with id: " + id);
        return shortStoryRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    public List<ShortStory> getByIds(Long[] ids) {
        LOGGER.debug("Returning short stories with ids: " + Arrays.toString(ids));
        List<ShortStory> out = new ArrayList<>();
        for (Long l : ids) {
            out.add(shortStoryRepository.findById(l).orElseThrow(ItemNotFoundException::new));
        }
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortStoryOutDto> getAll() {
        LOGGER.debug("Returning all short stories. NOAUTH.");
        return shortStoryRepository.getAllPublicDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortStoryOutDto> authedUserGetAll() {
        LOGGER.debug("Returning all short stories. AUTH.");
        return shortStoryRepository.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShortStory> getAllPaged(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortStory> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all short stores added by user: " + principal.getName());
        return shortStoryRepository.findAllByCreatedBy(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> modify(ShortStoryDto dto, Principal principal) {
        LOGGER.debug("Modifying short story (ADMIN): " + dto.toString());
        ShortStory shortStory = shortStoryRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);

        if (principal.getName().equals(shortStory.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
            shortStory = createOrCopyShortStory(shortStory, author, dto);
            shortStory = shortStoryRepository.saveAndFlush(shortStory);
            SearchRepository.updateShortStory(shortStory);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
