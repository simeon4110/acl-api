package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.persistence.dtos.PoemDto;
import com.sonnets.sonnet.persistence.dtos.sonnet.RejectDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.repositories.PoemRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class PoemService {
    private static final Logger LOGGER = Logger.getLogger(PoemService.class);
    private static final int NUMBER_OF_RANDOM_SONNETS = 2;
    private final PoemRepository poemRepository;
    private final SearchService searchService;
    private final MessageService messageService;
    private final AuthorService authorService;

    @Autowired
    public PoemService(PoemRepository poemRepository, SearchService searchService, MessageService messageService,
                       AuthorService authorService) {
        this.poemRepository = poemRepository;
        this.searchService = searchService;
        this.messageService = messageService;
        this.authorService = authorService;
    }

    /**
     * Parses text input into an Array for database storage.
     *
     * @param text a string[] of the text.
     * @return an ArrayList of the string[].
     */
    public static List<String> parseText(String text) {
        List<String> strings = new ArrayList<>();
        String[] textArr = text.split("\\r?\\n");

        for (String s : textArr) {
            strings.add(s.trim());
        }
        return strings;
    }

    public ResponseEntity<Void> add(PoemDto dto) {
        LOGGER.debug("Adding poem: " + dto.toString());
        // Check if poem already exists.
        try {
            searchService.similarExistsPoem(dto);
        } catch (ItemAlreadyExistsException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Author author = authorService.get(dto.getAuthorId());
        if (author != null) {
            Poem poem = new Poem();
            poemRepository.saveAndFlush(createOrUpdateFromDto(poem, dto, author));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> modify(PoemDto dto) {
        LOGGER.debug("Modifying poem (ADMIN): " + dto.toString());
        Optional<Poem> optionalPoem = poemRepository.findById(dto.getId());
        Author author = authorService.get(dto.getAuthorId());
        if (optionalPoem.isPresent() && author != null) {
            Poem poem = createOrUpdateFromDto(optionalPoem.get(), dto, author);
            poemRepository.saveAndFlush(poem);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> modify(PoemDto dto, Principal principal) {
        LOGGER.debug("Modifying poem (USER): " + dto.toString());
        Poem poem = getPoemOrNull(dto.getId().toString());
        Author author = authorService.get(dto.getAuthorId());
        // ensure poem was created by the user trying to modify it.
        if (poem != null && author != null && principal.getName().equals(poem.getCreatedBy())) {
            poemRepository.saveAndFlush(createOrUpdateFromDto(poem, dto, author));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> deleteById(String id) {
        LOGGER.debug("Deleting poem with id (ADMIN): " + id);
        Poem poem = getPoemOrNull(id);
        if (poem != null) {
            poemRepository.delete(poem);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> deleteById(String id, Principal principal) {
        LOGGER.debug("Deleting poem with id (USER): " + id);
        Poem poem = getPoemOrNull(id);
        if (poem != null && poem.getCreatedBy().equals(principal.getName())) {
            poemRepository.delete(poem);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> confirm(String id, Principal principal) {
        LOGGER.debug("Confirming sonnet: " + id);
        Poem poem = getPoemOrNull(id);
        if (poem != null) {
            poem.getConfirmation().setConfirmed(true);
            poem.getConfirmation().setConfirmedBy(principal.getName());
            poem.getConfirmation().setConfirmedAt(new Timestamp(System.currentTimeMillis()));
            poem.getConfirmation().setPendingRevision(false);
            poemRepository.saveAndFlush(poem);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> reject(RejectDto rejectDto) {
        LOGGER.debug("Rejecting poem: " + rejectDto.getId());
        Poem poem = getPoemOrNull(rejectDto.getId());
        if (poem != null) {
            poem.getConfirmation().setConfirmed(false);
            poem.getConfirmation().setPendingRevision(true);
            poemRepository.saveAndFlush(poem);

            MessageDto messageDto = new MessageDto();
            messageDto.setUserFrom("Administrator");
            messageDto.setUserTo(poem.getCreatedBy());
            messageDto.setSubject("One of your sonnets has been rejected.");
            messageDto.setContent(rejectDto.getRejectMessage());
            messageService.sendAdminMessage(messageDto);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public Poem getPoemToConfirm(Principal principal) {
        LOGGER.debug("Returning first unconfirmed poem not submitted by: " + principal.getName());
        List<Poem> poems = poemRepository
                .findAllByConfirmation_ConfirmedAndConfirmation_PendingRevision(false, false);
        for (Poem p : poems) {
            if (!p.getCreatedBy().equals(principal.getName())) {
                return p;
            }
        }
        return null;
    }

    public Poem getById(String id) {
        LOGGER.debug("Getting poem with id: " + id);
        return getPoemOrNull(id);
    }

    public List<Poem> getByIds(String[] ids) {
        LOGGER.debug("Getting poems with ids: " + Arrays.toString(ids));
        List<Poem> poems = new ArrayList<>();
        for (String s : ids) {
            Poem poem = this.getById(s);
            if (poem != null) {
                poems.add(poem);
            }
        }
        return poems;
    }

    public List<Poem> getTwoRandomSonnets() {
        LOGGER.debug("Returning two random sonnets.");
        Random random = new Random();
        List<Poem> sonnets = getAllByForm(Poem.Form.SONNET);
        List<Poem> randomSonnets = new ArrayList<>();

        while (randomSonnets.size() < NUMBER_OF_RANDOM_SONNETS) {
            Poem randomElement = sonnets.get(random.nextInt(sonnets.size()));
            randomSonnets.add(randomElement);
        }
        return randomSonnets;
    }

    public List<Poem> getAll() {
        LOGGER.debug("Returning all poems.");
        return poemRepository.findAll();
    }

    public Page<Poem> getAllPaged(Pageable pageable) {
        LOGGER.debug("Returning all poems paged.");
        return poemRepository.findAll(pageable);
    }

    public List<Poem> getAllByForm(final Poem.Form form) {
        LOGGER.debug("Returning all sonnets with form: " + form.toString());
        return poemRepository.findAllByForm(form);
    }

    public Page<Poem> getAllByFormPaged(final Poem.Form form, Pageable pageable) {
        LOGGER.debug("Returning all sonnets in category paged: " + form.toString());
        return poemRepository.findAllByForm(form, pageable);
    }

    public List<Poem> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all sonnets added by user: " + principal.getName());
        return poemRepository.findAllByCreatedBy(principal.getName());
    }

    private Poem createOrUpdateFromDto(Poem poem, PoemDto dto, Author author) {
        poem.setAuthor(author);
        poem.setTitle(dto.getTitle());
        poem.setPublicationYear(dto.getPublicationYear());
        poem.setPublicationStmt(dto.getPublicationStmt());
        poem.setSourceDesc(dto.getSourceDesc());
        poem.setPeriod(dto.getPeriod());
        poem.setForm(dto.getForm());
        poem.setText(parseText(dto.getText()));

        return poem;
    }

    private Poem getPoemOrNull(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return poemRepository.findById(parsedId).orElse(null);
    }
}
