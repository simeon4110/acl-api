package com.sonnets.sonnet.services.search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.ModelConstants;
import com.sonnets.sonnet.persistence.models.annotation_types.Dialog;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Section;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
    private final EntityManager entityManager;

    @Autowired
    public SearchQueryHandlerService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public JSONObject doSearch(SearchDto dto) {
        LOGGER.debug("Parsing dto into search: " + dto.toString());
        Gson gson = new Gson();
        Gson sectionGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        try {
            FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
            JSONObject results = new JSONObject();
            if (dto.isSearchDialog()) {
                Query query = DialogHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Dialog.class);
                results.put(ModelConstants.TYPE_DIALOG, new JSONArray(gson.toJson(fullTextQuery.getResultList())));
            }
            if (dto.isSearchPoems()) {
                Query query = PoemHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Poem.class);
                results.put(ModelConstants.TYPE_POEM, new JSONArray(gson.toJson(fullTextQuery.getResultList())));
            }
            if (dto.isSearchBookCharacters()) {
                Query query = CharacterHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, BookCharacter.class);
                results.put(ModelConstants.TYPE_CHARACTER, new JSONArray(gson.toJson(fullTextQuery.getResultList())));
            }
            if (dto.isSearchBooks()) { // :todo: fix this when you give a shit.
                Query query = SectionHandler.getQuery(dto);
                FullTextQuery fullTextQuery = manager.createFullTextQuery(query, Section.class);
                LOGGER.debug("BOOK OUTPUT: " + sectionGson.toJson(fullTextQuery.getResultList()));
                results.put(ModelConstants.TYPE_SECTION, new JSONArray(
                        sectionGson.toJson(fullTextQuery.getResultList())));
                LOGGER.debug("BOOK OUTPUT: " + fullTextQuery.getResultList().toString());
            }
            LOGGER.debug("Found: " + results.length());
            return results;
        } catch (JSONException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public List searchAuthor(AuthorDto dto) {
        LOGGER.debug("Searching for author: " + dto.toString());
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(AuthorHandler.getQuery(dto), Author.class);
        return fullTextQuery.getResultList();
    }

    public void similarExistsPoem(SearchDto dto) {
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        query.add(new TermQuery(new Term(SearchConstants.AUTHOR_LAST_NAME, dto.getAuthor().getLastName())),
                BooleanClause.Occur.MUST);
        query.add(new TermQuery(new Term(SearchConstants.TITLE, dto.getTitle())), BooleanClause.Occur.MUST);
        FullTextQuery fullTextQuery = manager.createFullTextQuery(query.build(), Poem.class);
        if (fullTextQuery.getResultSize() != 0) {
            LOGGER.error("Found similar poems.");
            throw new ItemAlreadyExistsException("Item: '" + dto.toString() + "' already exists.");
        }
    }
}
