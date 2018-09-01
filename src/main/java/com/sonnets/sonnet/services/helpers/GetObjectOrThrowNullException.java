package com.sonnets.sonnet.services.helpers;

import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper component for controlling object pulls from the database. Returns null if the ID is not a valid long, or the
 * object doesn't exist in the database. Doing it like this prevents import overload in every related class object. I
 * know this isn't the most 'graceful' way to do things, but it keeps the services a lot cleaner.
 *
 * @author Josh Harkema
 */
@Component
public class GetObjectOrThrowNullException {
    private static final Logger LOGGER = Logger.getLogger(GetObjectOrThrowNullException.class);
    private final BookRepository bookRepository;
    private final SectionRepositoryBase sectionRepository;
    private final CharacterRepository characterRepository;
    private final AuthorRepository authorRepository;
    private final OtherRepository otherRepository;
    private final PoemRepository poemRepository;
    private final AnnotationRepository annotationRepository;

    @Autowired
    public GetObjectOrThrowNullException(BookRepository bookRepository, SectionRepositoryBase sectionRepository,
                                         CharacterRepository characterRepository, AuthorRepository authorRepository,
                                         OtherRepository otherRepository, PoemRepository poemRepository,
                                         AnnotationRepository annotationRepository) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.characterRepository = characterRepository;
        this.authorRepository = authorRepository;
        this.otherRepository = otherRepository;
        this.poemRepository = poemRepository;
        this.annotationRepository = annotationRepository;
    }

    public Poem poem(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return poemRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Book book(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return bookRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Author author(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return authorRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Section section(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return sectionRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public BookCharacter character(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return characterRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Other other(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return otherRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Annotation annotation(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return annotationRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }
}
