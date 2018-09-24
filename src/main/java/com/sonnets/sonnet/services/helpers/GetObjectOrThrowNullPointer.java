package com.sonnets.sonnet.services.helpers;

import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.persistence.repositories.*;
import com.sonnets.sonnet.persistence.repositories.book.BookRepository;
import com.sonnets.sonnet.persistence.repositories.corpora.CorporaRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.persistence.repositories.section.SectionRepositoryBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper component for controlling object pulls from the database. Returns null if the ID is not a valid long, or the
 * object doesn't exist in the database. Doing it like this prevents import overload in every related class object. I
 * know this isn't the most 'graceful' way to do things, but it keeps the services a lot cleaner.
 *
 * @author Josh Harkema
 */
@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class GetObjectOrThrowNullPointer {
    private final BookRepository bookRepository;
    private final SectionRepositoryBase sectionRepository;
    private final CharacterRepository characterRepository;
    private final AuthorRepository authorRepository;
    private final OtherRepository otherRepository;
    private final PoemRepository poemRepository;
    private final AnnotationRepository annotationRepository;
    private final CorporaRepository corporaRepository;
    private final CustomStopWordsRepository customStopWordsRepository;

    @Autowired
    public GetObjectOrThrowNullPointer(BookRepository bookRepository, SectionRepositoryBase sectionRepository,
                                       CharacterRepository characterRepository, AuthorRepository authorRepository,
                                       OtherRepository otherRepository, PoemRepository poemRepository,
                                       AnnotationRepository annotationRepository, CorporaRepository corporaRepository,
                                       CustomStopWordsRepository customStopWordsRepository) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.characterRepository = characterRepository;
        this.authorRepository = authorRepository;
        this.otherRepository = otherRepository;
        this.poemRepository = poemRepository;
        this.annotationRepository = annotationRepository;
        this.corporaRepository = corporaRepository;
        this.customStopWordsRepository = customStopWordsRepository;
    }

    public Poem poem(String id) {
        long parsedId = Long.parseLong(id);
        return poemRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Book book(String id) {
        long parsedId = Long.parseLong(id);
        return bookRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Author author(String id) {
        long parsedId = Long.parseLong(id);
        return authorRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Section section(String id) {
        long parsedId = Long.parseLong(id);
        return sectionRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public BookCharacter character(String id) {
        long parsedId = Long.parseLong(id);
        return characterRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Other other(String id) {
        long parsedId = Long.parseLong(id);
        return otherRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Annotation annotation(String id) {
        long parsedId = Long.parseLong(id);
        return annotationRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public Corpora corpora(String id) {
        long parsedId = Long.parseLong(id);
        return corporaRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }

    public CustomStopWords stopWords(String id) {
        long parsedId = Long.parseLong(id);
        return customStopWordsRepository.findById(parsedId).orElseThrow(NullPointerException::new);
    }
}
