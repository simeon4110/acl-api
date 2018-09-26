package com.sonnets.sonnet.services.helpers;

import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.BookCharacterRepository;
import com.sonnets.sonnet.persistence.repositories.OtherRepository;
import com.sonnets.sonnet.persistence.repositories.book.BookRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.persistence.repositories.section.SectionRepositoryBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper component for handling saves to the database. Also used for keeping the services clean.
 *
 * @author Josh Harkema
 */
@Component
public class SaveObject {
    private final BookRepository bookRepository;
    private final SectionRepositoryBase sectionRepository;
    private final BookCharacterRepository bookCharacterRepository;
    private final AuthorRepository authorRepository;
    private final OtherRepository otherRepository;
    private final PoemRepository poemRepository;

    @Autowired
    public SaveObject(BookRepository bookRepository, SectionRepositoryBase sectionRepository,
                      BookCharacterRepository bookCharacterRepository, AuthorRepository authorRepository,
                      OtherRepository otherRepository, PoemRepository poemRepository) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.bookCharacterRepository = bookCharacterRepository;
        this.authorRepository = authorRepository;
        this.otherRepository = otherRepository;
        this.poemRepository = poemRepository;
    }

    public void poem(Poem poem) {
        poemRepository.saveAndFlush(poem);
    }

    public void book(Book book) {
        bookRepository.saveAndFlush(book);
    }

    public void author(Author author) {
        authorRepository.saveAndFlush(author);
    }

    public void section(Section section) {
        sectionRepository.saveAndFlush(section);
    }

    public void character(BookCharacter character) {
        bookCharacterRepository.saveAndFlush(character);
    }

    public void other(Other other) {
        otherRepository.saveAndFlush(other);
    }
}
