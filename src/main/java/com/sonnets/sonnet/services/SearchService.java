package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
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
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

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
    private static final String SOURCE = "source";
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
        if (dto.getCategory() != null) {
            LOGGER.debug("Category: " + dto.getCategory());
            query.add(new TermQuery(new Term(CATEGORY, dto.getCategory().toLowerCase())), BooleanClause.Occur.MUST);
        }

        // Add author.
        if (dto.getAuthor() != null) {
            // Add author first name.
            if (dto.getAuthor().getFirstName() != null) {
                LOGGER.debug("Author first name: " + dto.getAuthor().getFirstName());
                query.add(new TermQuery(new Term(AUTHOR_FIRST_NAME, dto.getAuthor().getFirstName().toLowerCase())),
                        BooleanClause.Occur.MUST);
            }
            // Add author last name.
            if (dto.getAuthor().getLastName() != null) {
                LOGGER.debug("Author last name: " + dto.getAuthor().getLastName());
                query.add(new TermQuery(new Term(AUTHOR_LAST_NAME, dto.getAuthor().getLastName().toLowerCase())),
                        BooleanClause.Occur.MUST);
            }
        }

        // Add title.
        if (dto.getTitle() != null) {
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
        if (dto.getTopics() != null) {
            LOGGER.debug("Topics: " + dto.getTopics());
            query.add(new TermQuery(new Term(PERIOD, dto.getPeriod().toLowerCase())), BooleanClause.Occur.MUST);
        }

        // Add text.
        if (dto.getText() != null) {
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
        if (dto.getForm() != null) {
            LOGGER.debug("Poem FORM: " + dto.getForm());
            builder.add(new TermQuery(new Term(POEM_FORM, dto.getForm().toLowerCase())), BooleanClause.Occur.MUST);
        }
    }

    // Adds book specific search params.
    private static void parseBookParams(BooleanQuery.Builder builder, SearchDto dto) {
        LOGGER.debug("Parsing BOOK params.");
        if (dto.getType() != null) {
            LOGGER.debug("Parsing BOOK TYPE: " + dto.getType());
            builder.add(new TermQuery(new Term(BOOK_TYPE, dto.getType().toLowerCase())), BooleanClause.Occur.MUST);
        }
    }

    // Adds character specific search params.
    private static void parseBookCharacterParams(BooleanQuery.Builder builder, SearchDto dto) {
        LOGGER.debug("Parsing BOOK CHARACTER params.");
        if (dto.getCharLastName() != null) {
            LOGGER.debug("Parsing CHAR FIRST NAME: " + dto.getCharFirstName());
            builder.add(new FuzzyQuery(new Term(BOOK_CHARACTER_FN, dto.getCharFirstName().toLowerCase()),
                    EDIT_DISTANCE, PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (dto.getCharLastName() != null) {
            LOGGER.debug("Parsing CHAR LAST NAME: " + dto.getCharLastName());
            builder.add(new FuzzyQuery(new Term(BOOK_CHARACTER_LN, dto.getCharLastName().toLowerCase()),
                    EDIT_DISTANCE, PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (dto.getCharGender() != null) {
            LOGGER.debug("Parsing CHAR GENDER: " + dto.getCharGender().toLowerCase());
            builder.add(new TermQuery(new Term(BOOK_CHARACTER_SEX, dto.getCharGender())), BooleanClause.Occur.MUST);
        }
    }

    // Public entry method.
    public Page search(SearchDto dto, Pageable pageable) {
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        Query query = parseQuery(dto);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(query,
                Book.class, Other.class, Section.class, Poem.class);

        @SuppressWarnings("unchecked")
        List<Poem> results = fullTextQuery.getResultList();
        LOGGER.debug("Found total records: " + fullTextQuery.getResultSize());
        return new PageImpl<>(results, pageable, fullTextQuery.getResultSize());
    }

    public void similarExistsPoem(PoemDto poemDto) {
        LOGGER.debug("Similar poem search: " + poemDto.toString());
        Query query;
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = manager.getSearchFactory().buildQueryBuilder().forEntity(Poem.class).get();
        Author author = authorService.get(poemDto.getAuthorId());
        if (author == null) {
            throw new NullPointerException("Author with id '" + poemDto.getAuthorId() + "' does not exist.");
        }

        // Use first line as title if title is null.
        if (Objects.equals(poemDto.getTitle(), "")) {
            poemDto.setTitle(PoemService.parseText(poemDto.getText()).get(0));
        }

        // Boolean "must" query searches for the exact title, last name and source of a sonnet.
        query = queryBuilder.bool()
                .must(queryBuilder.phrase().onField(TITLE).sentence(poemDto.getTitle()).createQuery())
                .must(queryBuilder.keyword().onField(AUTHOR_LAST_NAME).matching(
                        author.getLastName()).createQuery()
                )
                .must(queryBuilder.keyword().onField(POEM_FORM).matching(poemDto.getForm()).createQuery())
                .must(queryBuilder.phrase().onField(SOURCE).sentence(poemDto.getSourceDesc()).createQuery())
                .createQuery();

        FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Poem.class);

        List results = fullTextQuery.getResultList();

        LOGGER.debug("Found similar: " + results.toString());

        // If the search turned up any results, throw a SonnetAlreadyExists exception.
        if (!results.isEmpty()) {
            throw new ItemAlreadyExistsException("Poem by: " + author.getLastName()
                    + "With title: " + poemDto.getTitle()
                    + " Already exists.");
        }
    }
}
