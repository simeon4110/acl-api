package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Book;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.base.Section;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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
 * Routes a SearchDto into queries based on boolean flags set in the dto.
 *
 * @author Josh Harkema
 */
@Service
@Transactional
public class SearchQueryHandlerService {
    private static final Logger LOGGER = Logger.getLogger(SearchQueryHandlerService.class);
    private final StandardAnalyzer standardAnalyzer;
    private final EntityManager entityManager;

    @Autowired
    public SearchQueryHandlerService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.standardAnalyzer = new StandardAnalyzer();
    }

    public List doSearch(String queryString) {
        LOGGER.debug("Parsing query string: " + queryString);
        try {
            Query q = new QueryParser(null, standardAnalyzer).parse(queryString);
            FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
            FullTextQuery fullTextQuery = manager.createFullTextQuery(q, Poem.class, Book.class, Section.class);
            return fullTextQuery.getResultList();
        } catch (ParseException e) {
            LOGGER.error(e);
            return Collections.emptyList();
        }
    }

    public List searchAuthor(AuthorDto dto) throws ParseException {
        LOGGER.debug("Searching for author: " + dto.toString());
        String queryString = "firstName: \"" + dto.getFirstName() + "\" AND lastName: \"" + dto.getLastName() + "\"";
        Query q = new QueryParser(null, standardAnalyzer).parse(queryString);
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(q, Author.class);
        return fullTextQuery.getResultList();
    }

    public void similarExistsPoem(SearchDto dto) {
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        query.add(new TermQuery(new Term(SearchConstants.AUTHOR_LAST_NAME, dto.getAuthor().getLastName())),
                BooleanClause.Occur.MUST);

        // Build out a phrase query for the title search.
        PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
        phraseQuery.setSlop(SearchConstants.SLOP);
        int counter = 0;
        for (String term : dto.getTitle().split(" ")) {
            phraseQuery.add(new Term(SearchConstants.TITLE, term.toLowerCase()), counter);
            counter++;
        }
        query.add(phraseQuery.build(), BooleanClause.Occur.MUST);

        FullTextQuery fullTextQuery = manager.createFullTextQuery(query.build(), Poem.class);
        if (fullTextQuery.getResultSize() != 0) {
            LOGGER.error("Found similar poems.");
            throw new ItemAlreadyExistsException("Item: '" + dto.toString() + "' already exists.");
        }
    }
}
