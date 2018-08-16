package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.PoemDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
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
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles dynamic BooleanQuery building and other Lucene focused search jobs.
 *
 * @author Josh Harkema
 */
@Service
public class SearchService {
    private final EntityManager entityManager;
    private static final int PRECISION_STEP = 2;
    private static final Logger LOGGER = Logger.getLogger(SearchService.class);

    // Lucene fuzzy and phrase query constants.
    private static final int PREFIX_LENGTH = 0; // How many chars are "fixed" to the front.
    private static final int EDIT_DISTANCE = 2; // Levenstein edit distance.
    private static final int MAX_EXPANSIONS = 2; // How many words must match.
    private static final int SLOP = 1; // Words are directly adjacent.
    private static final String YEAR = "publicationYear";

    // Field name constants:
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String TITLE = "title";
    private static final String FORM = "form";
    private static final String PERIOD = "period";
    private static final String TEXT = "text";
    private final AuthorService authorService;
    private static final String SOURCE = "source";


    @Autowired
    public SearchService(EntityManager entityManager, AuthorService authorService) {
        this.entityManager = entityManager;
        this.authorService = authorService;
    }

    public List<Long> getResultIdsPoem(final String firstName, final String lastName, final String title,
                                       final int publicationYear, final String period, final String text,
                                       final Poem.Form form) {
        List<Long> ids = new ArrayList<>();

        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        // Build and execute query.
        org.apache.lucene.search.Query query = buildQueryProse(
                firstName, lastName, title, publicationYear, period, text, form
        );
        FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Poem.class);

        @SuppressWarnings("unchecked")
        List<Poem> result = fullTextQuery.getResultList();

        for (Poem p : result) {
            ids.add(p.getId());
        }

        return ids;
    }

    public Page<Poem> searchPoems(final String firstName, final String lastName, final String title,
                                  final int publicationYear, final String period, final String text,
                                  final Poem.Form form, final Pageable pageable) {
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        // Build and execute query.
        org.apache.lucene.search.Query query = buildQueryProse(
                firstName, lastName, title, publicationYear, period, text, form
        );
        FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Poem.class);

        @SuppressWarnings("unchecked")
        List<Poem> results = fullTextQuery.getResultList();
        LOGGER.debug("Found total records: " + fullTextQuery.getResultSize());
        return new PageImpl<>(results, pageable, fullTextQuery.getResultSize());
    }


    /**
     * Builds out the lucene query as a series of Must boolean clauses.
     *
     * @param firstName author's first name.
     * @param lastName  author's last name.
     * @param title     title of the sonnet.
     * @param period    period of publication.
     * @param text      text contained in the sonnet.
     * @return a built lucene query.
     */
    private org.apache.lucene.search.Query buildQueryProse(final String firstName, final String lastName,
                                                           final String title, final int publicationYear,
                                                           final String period, final String text,
                                                           final Poem.Form form) {
        LOGGER.debug("Parsing search: " + firstName + " " + lastName + " " + title + " " + period + " " + text);
        BooleanQuery.Builder booleanClauses = new BooleanQuery.Builder();

        // Add first name.
        if (!Objects.equals(firstName, "") && firstName != null) {
            LOGGER.debug(FIRST_NAME);
            TermQuery tq = new TermQuery(new Term(FIRST_NAME, firstName));
            booleanClauses.add(tq, BooleanClause.Occur.MUST);
        }

        // Add last name.
        if (!Objects.equals(lastName, "") && lastName != null) {
            LOGGER.debug(LAST_NAME);
            FuzzyQuery fq = new FuzzyQuery(
                    new Term(LAST_NAME, lastName),
                    EDIT_DISTANCE, PREFIX_LENGTH, MAX_EXPANSIONS, true);
            booleanClauses.add(fq, BooleanClause.Occur.MUST);
        }

        // Add title.
        if (!Objects.equals(title, "") && title != null) {
            LOGGER.debug(TITLE);
            FuzzyQuery fq = new FuzzyQuery(
                    new Term(TITLE, title), EDIT_DISTANCE, PREFIX_LENGTH);
            booleanClauses.add(fq, BooleanClause.Occur.MUST);
        }

        // Add publication year.
        if (publicationYear > 0) {
            LOGGER.debug(YEAR);
            NumericRangeQuery rangeQuery = NumericRangeQuery.newIntRange(
                    YEAR, PRECISION_STEP, publicationYear + 20, publicationYear - 20, true,
                    true);
            booleanClauses.add(rangeQuery, BooleanClause.Occur.MUST);
        }

        // Add period.
        if (!Objects.equals(period, "") && period != null) {
            LOGGER.debug(PERIOD);
            TermQuery tq = new TermQuery(new Term(PERIOD, period));
            booleanClauses.add(tq, BooleanClause.Occur.MUST);
        }

        // Add text search.
        if (!Objects.equals(text, "") && text != null) {
            LOGGER.debug(TEXT);
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            builder.setSlop(SLOP);
            int counter = 0;
            for (String term : text.split(" ")) {
                builder.add(new Term(TEXT, term), counter);
                counter++;
            }
            booleanClauses.add(builder.build(), BooleanClause.Occur.MUST);
        }

        // Add form search.
        if (form != null) {
            LOGGER.debug(FORM);
            TermQuery tq = new TermQuery(new Term(FORM, form.toString()));
            booleanClauses.add(tq, BooleanClause.Occur.MUST);
        }

        return booleanClauses.build();
    }

    public void similarExistsPoem(PoemDto poemDto) {
        LOGGER.debug("Similar poem search: " + poemDto.toString());
        org.apache.lucene.search.Query query;
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
                .must(queryBuilder.keyword().onField(LAST_NAME).matching(
                        author.getLastName()).createQuery()
                )
                .must(queryBuilder.keyword().onField(FORM).matching(poemDto.getForm().toString()).createQuery())
                .must(queryBuilder.phrase().onField(SOURCE).sentence(poemDto.getSourceDesc()).createQuery())
                .createQuery();

        Query fullTextQuery = manager.createFullTextQuery(query, Poem.class);

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
