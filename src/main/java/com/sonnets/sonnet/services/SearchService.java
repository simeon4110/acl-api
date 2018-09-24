package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.models.prose.Section;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

/**
 * Handles dynamic BooleanQuery building and other Lucene focused search jobs. It is overly complex because a simple
 * method for implementing dynamic queries does not currently exist.
 *
 * @author Josh Harkema
 */
@Service
public class SearchService {
    private final EntityManager entityManager;
    private static final Logger LOGGER = Logger.getLogger(SearchService.class);
    // Numeric range query settings.
    private static final int INT_DISTANCE = 20;

    // General field names.
    private static final String CATEGORY = "category";
    private static final int PRECISION_STEP = 2;

    // Lucene fuzzy and phrase query constants.
    private static final int PREFIX_LENGTH = 0; // How many chars are "fixed" to the front.
    private static final int EDIT_DISTANCE = 2; // Levenstein edit distance.
    private static final int SLOP = 1; // Words are directly adjacent.
    private static final String AUTHOR_FIRST_NAME = "author.firstName";
    private static final String AUTHOR_LAST_NAME = "author.lastName";
    private static final String YEAR = "publicationYear";
    private static final String TITLE = "title";
    private static final String PERIOD = "period";
    // Item specific field names.
    private static final String POEM_FORM = "poem_form";
    private static final String TEXT = "text";
    private static final String BOOK_TYPE = "book_type";
    private static final String BOOK_CHARACTER_FN = "character_first_name";
    private static final String BOOK_CHARACTER_LN = "character_last_name";
    private static final String BOOK_CHARACTER_SEX = "character_gender";
    private final AuthorService authorService;

    @Autowired
    public SearchService(EntityManager entityManager, AuthorService authorService) {
        this.entityManager = entityManager;
        this.authorService = authorService;
    }

    /**
     * Queries are parsed based on null / not-null values from a search DTO. One field, or all fields can be searched
     * simultaneously.
     *
     * @param dto a valid search dto.
     * @return a parsed query.
     */
    private static Query parseQuery(SearchDto dto) {
        LOGGER.debug("Parsing query: " + dto.toString());
        BooleanQuery.Builder query = new BooleanQuery.Builder();

        // Add category.
        if (dto.getCategory() != null && !dto.getCategory().equals("")) {
            LOGGER.debug("Category: " + dto.getCategory());
            query.add(new TermQuery(new Term(CATEGORY, dto.getCategory().toLowerCase())), BooleanClause.Occur.MUST);
        }

        // Add author.
        if (dto.getAuthor() != null) {
            // Add author first name.
            if (dto.getAuthor().getFirstName() != null && !dto.getAuthor().getFirstName().equals("")) {
                LOGGER.debug("Author first name: " + dto.getAuthor().getFirstName());
                query.add(new TermQuery(new Term(AUTHOR_FIRST_NAME, dto.getAuthor().getFirstName().toLowerCase())),
                        BooleanClause.Occur.MUST);
            }
            // Add author last name.
            if (dto.getAuthor().getLastName() != null && !dto.getAuthor().getLastName().equals("")) {
                LOGGER.debug("Author last name: " + dto.getAuthor().getLastName());
                query.add(new TermQuery(new Term(AUTHOR_LAST_NAME, dto.getAuthor().getLastName().toLowerCase())),
                        BooleanClause.Occur.MUST);
            }
        }

        // Add title.
        if (dto.getTitle() != null && !dto.getTitle().equals("")) {
            LOGGER.debug("Title: " + dto.getTitle());
            query.add(new FuzzyQuery(new Term(TITLE, dto.getTitle().toLowerCase()), EDIT_DISTANCE, PREFIX_LENGTH),
                    BooleanClause.Occur.MUST);
        }

        // Add publication year as a numeric range query.
        if (dto.getPublicationYear() != 0) {
            LOGGER.debug("Year: " + dto.getPublicationYear());
            query.add(NumericRangeQuery.newIntRange(
                    YEAR, PRECISION_STEP, dto.getPublicationYear() - INT_DISTANCE,
                    dto.getPublicationYear() + INT_DISTANCE, true, true
            ), BooleanClause.Occur.MUST);
        }

        // Add topics.
        if (dto.getTopics() != null && !dto.getTopics().equals("")) {
            LOGGER.debug("Topics: " + dto.getTopics());
            query.add(new TermQuery(new Term(PERIOD, dto.getPeriod().toLowerCase())), BooleanClause.Occur.MUST);
        }

        // Add text.
        if (dto.getText() != null && !dto.getText().equals("")) {
            LOGGER.debug("Text: " + dto.getText());
            PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
            phraseQuery.setSlop(SLOP);
            int counter = 0;
            for (String term : dto.getText().split(" ")) {
                phraseQuery.add(new Term(TEXT, term.toLowerCase()), counter);
                counter++;
            }
            query.add(phraseQuery.build(), BooleanClause.Occur.MUST);
        }

        if (dto.isSearchPoems()) {
            parsePoemParams(query, dto);
        }
        if (dto.isSearchBooks()) {
            parseBookParams(query, dto);
        }
        if (dto.isSearchBookCharacters()) {
            parseBookCharacterParams(query, dto);
        }

        return query.build();
    }

    // Adds poem specific search params.
    private static void parsePoemParams(BooleanQuery.Builder builder, SearchDto dto) {
        LOGGER.debug("Parsing POEM params.");
        if (dto.getForm() != null && !dto.getForm().equals("")) {
            LOGGER.debug("Poem FORM: " + dto.getForm());
            builder.add(new TermQuery(new Term(POEM_FORM, dto.getForm())), BooleanClause.Occur.MUST);
        }
    }

    // Adds book specific search params.
    private static void parseBookParams(BooleanQuery.Builder builder, SearchDto dto) {
        LOGGER.debug("Parsing BOOK params.");
        if (dto.getType() != null && !dto.getType().equals("")) {
            LOGGER.debug("Parsing BOOK TYPE: " + dto.getType());
            builder.add(new TermQuery(new Term(BOOK_TYPE, dto.getType().toLowerCase())), BooleanClause.Occur.MUST);
        }
    }

    // Adds character specific search params.
    private static void parseBookCharacterParams(BooleanQuery.Builder builder, SearchDto dto) {
        LOGGER.debug("Parsing BOOK CHARACTER params.");
        if (dto.getCharFirstName() != null && !dto.getCharFirstName().equals("")) {
            LOGGER.debug("Parsing CHAR FIRST NAME: " + dto.getCharFirstName());
            builder.add(new FuzzyQuery(new Term(BOOK_CHARACTER_FN, dto.getCharFirstName().toLowerCase()),
                    EDIT_DISTANCE, PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (dto.getCharLastName() != null && !dto.getCharLastName().equals("")) {
            LOGGER.debug("Parsing CHAR LAST NAME: " + dto.getCharLastName());
            builder.add(new FuzzyQuery(new Term(BOOK_CHARACTER_LN, dto.getCharLastName().toLowerCase()),
                    EDIT_DISTANCE, PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (dto.getCharGender() != null && !dto.getCharGender().equals("")) {
            LOGGER.debug("Parsing CHAR GENDER: " + dto.getCharGender().toLowerCase());
            builder.add(new TermQuery(new Term(BOOK_CHARACTER_SEX, dto.getCharGender())), BooleanClause.Occur.MUST);
        }
    }

    // Public entry method.
    @Transactional(readOnly = true)
    public List search(SearchDto dto) {
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        Query query = parseQuery(dto);
        FullTextQuery fullTextQuery;
        if (dto.isSearchBooks() && dto.isSearchPoems()) {
            fullTextQuery = manager.createFullTextQuery(query, Book.class, Section.class, Poem.class);
        } else if (dto.isSearchPoems()) {
            fullTextQuery = manager.createFullTextQuery(query, Poem.class);
        } else if (dto.isSearchBooks()) {
            fullTextQuery = manager.createFullTextQuery(query, Book.class, Section.class);
        } else {
            fullTextQuery = manager.createFullTextQuery(query, Book.class, Section.class, Poem.class, Other.class);
        }

        @SuppressWarnings("unchecked")
        List<Poem> results = fullTextQuery.getResultList();
        LOGGER.debug("Found total records: " + fullTextQuery.getResultSize());
        return results;
    }

    @Transactional(readOnly = true)
    public List searchAuthor(AuthorDto authorDto) {
        LOGGER.debug("Searching for author: " + authorDto.toString());
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        // Add author first name.
        if (authorDto.getFirstName() != null && !authorDto.getFirstName().equals("")) {
            LOGGER.debug("Author first name: " + authorDto.getFirstName());
            builder.add(new TermQuery(new Term("firstName", authorDto.getFirstName().toLowerCase())),
                    BooleanClause.Occur.SHOULD);
        }
        // Add author last name.
        if (authorDto.getLastName() != null && !authorDto.getLastName().equals("")) {
            LOGGER.debug("Author last name: " + authorDto.getLastName());
            builder.add(new TermQuery(new Term("lastName", authorDto.getLastName().toLowerCase())),
                    BooleanClause.Occur.SHOULD);
        }
        FullTextQuery fullTextQuery = manager.createFullTextQuery(builder.build(), Author.class);

        List results = fullTextQuery.getResultList();
        LOGGER.debug("Found: " + results.toString());
        return results;
    }

    @Transactional(readOnly = true)
    public List searchBooks(BookDto bookDto) {
        LOGGER.debug("Searching for books: " + bookDto);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        Author author = authorService.get(bookDto.getAuthorId());
        if (author != null) {
            if (!author.getFirstName().equals("") && author.getFirstName() != null) {
                LOGGER.debug("First name: " + author.getFirstName());
                builder.add(new TermQuery(
                        new Term(AUTHOR_FIRST_NAME, author.getFirstName().toLowerCase())), BooleanClause.Occur.SHOULD
                );
            }

            LOGGER.debug("Book title: " + bookDto.getTitle());
            builder.add(new TermQuery(new Term(TITLE, bookDto.getTitle().toLowerCase())), BooleanClause.Occur.SHOULD);

            LOGGER.debug("Last name: " + author.getLastName());
            builder.add(new TermQuery(new Term(AUTHOR_LAST_NAME, author.getLastName().toLowerCase())),
                    BooleanClause.Occur.SHOULD);

            FullTextQuery fullTextQuery = manager.createFullTextQuery(builder.build(), Book.class, Author.class);

            List results = fullTextQuery.getResultList();
            LOGGER.debug("Found: " + fullTextQuery.getResultList());
            return results;
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public void similarExistsPoem(PoemDto poemDto) {
        LOGGER.debug("Similar poem search: " + poemDto.toString());
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        FullTextQuery fullTextQuery;

        Author author = authorService.get(poemDto.getAuthorId());
        if (author != null) {
            query.add(new TermQuery(new Term(AUTHOR_LAST_NAME, author.getLastName())), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term(TITLE, poemDto.getTitle())), BooleanClause.Occur.MUST);
            fullTextQuery = manager.createFullTextQuery(query.build(), Poem.class);
            LOGGER.debug("Found: " + fullTextQuery.getResultSize());
            if (fullTextQuery.getResultSize() != 0) {
                throw new ItemAlreadyExistsException("Item: '" + poemDto.toString() + "' already exists.");
            }
        }
    }
}
