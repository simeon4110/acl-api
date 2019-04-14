package com.sonnets.sonnet.services.base;

import com.sonnets.sonnet.config.LuceneConfig;
import com.sonnets.sonnet.persistence.dtos.base.ShortStoryDto;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.ShortStory;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.ShortStoryRepository;
import com.sonnets.sonnet.services.AbstractItemService;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.search.SearchCRUDService;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.ParseSourceDetails;

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
public class ShortStoryService implements AbstractItemService<ShortStory, ShortStoryDto> {
    private static final Logger LOGGER = Logger.getLogger(ShortStoryService.class);
    private static final ParseSourceDetails<ShortStory, ShortStoryDto> parseSourceDetails = new ParseSourceDetails<>();
    private final ShortStoryRepository shortStoryRepository;
    private final AuthorRepository authorRepository;
    private final TaskExecutor executor;

    @Autowired
    public ShortStoryService(ShortStoryRepository shortStoryRepository, AuthorRepository authorRepository,
                             @Qualifier("threadPoolTaskExecutor") TaskExecutor executor) {
        this.shortStoryRepository = shortStoryRepository;
        this.authorRepository = authorRepository;
        this.executor = executor;
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
        LOGGER.debug("Adding new ShortStory search document...");
        Document document = SearchCRUDService.parseCommonFields(new Document(), shortStory);
        document.add(LuceneConfig.getTextField(shortStory.getText()));
        SearchCRUDService.addDocument(document, TypeConstants.SHORT_STORY);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> add(ShortStoryDto dto) {
        LOGGER.debug("Adding new short story: " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        ShortStory shortStory = createOrCopyShortStory(new ShortStory(), author, dto);
        addNewSearchDocument(shortStoryRepository.save(shortStory));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting short story with id (ADMIN): " + id);
        ShortStory shortStory = shortStoryRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        executor.execute(() -> {
            shortStoryRepository.delete(shortStory);
            SearchCRUDService.deleteDocument(String.valueOf(id), TypeConstants.SHORT_STORY);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> userDelete(Long id, Principal principal) {
        LOGGER.debug("Deleting short story with id (USER): " + id);
        ShortStory shortStory = shortStoryRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (shortStory.getCreatedBy().equals(principal.getName())) {
            executor.execute(() -> {
                shortStoryRepository.delete(shortStory);
                SearchCRUDService.deleteDocument(String.valueOf(id), TypeConstants.SHORT_STORY);
            });
            return new ResponseEntity<>(HttpStatus.OK);
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
    public List<ShortStory> getAll() {
        LOGGER.debug("Returning all short stories. NOAUTH.");
        return shortStoryRepository.findAllByIsPublicDomain(true).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortStory> authedUserGetAll() {
        LOGGER.debug("Returning all short stories. AUTH.");
        return shortStoryRepository.findAll();
    }

    @Override
    @Transactional
    public String getAllSimple() {
        return null;
    }

    @Override
    @Transactional
    public String authedUserGetAllSimple() {
        return null;
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
    public ResponseEntity<Void> modify(ShortStoryDto dto) {
        LOGGER.debug("Modifying short story (ADMIN): " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        ShortStory shortStory = shortStoryRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        shortStory = createOrCopyShortStory(shortStory, author, dto);
        shortStory = shortStoryRepository.saveAndFlush(shortStory);
        SearchCRUDService.updateShortStory(shortStory);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> modifyUser(ShortStoryDto dto, Principal principal) {
        LOGGER.debug("Modifying short story (USER): " + dto.toString());
        ShortStory shortStory = shortStoryRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        if (shortStory.getCreatedBy().equals(principal.getName())) {
            return modify(dto);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
