package org.acl.database.config;

import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.base.Poem;
import org.acl.database.persistence.models.base.Section;
import org.acl.database.persistence.models.base.ShortStory;
import org.acl.database.persistence.models.theater.Play;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.persistence.repositories.SectionRepositoryBase;
import org.acl.database.persistence.repositories.ShortStoryRepository;
import org.acl.database.persistence.repositories.poem.PoemRepository;
import org.acl.database.persistence.repositories.theater.PlayRepository;
import org.acl.database.search.SearchRepository;
import org.acl.database.services.search.SearchConstants;
import org.acl.database.services.theater.PlayService;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.*;

/**
 * This is where the Lucene indexes are built. Runs after db initialization and writes Lucene indexes to
 * INDEX_DIR defined in SearchConstants.
 *
 * @author Josh Harkema
 */
@Configuration
public class LuceneConfig {
    private static final Logger LOGGER = Logger.getLogger(LuceneConfig.class);
    private final PoemRepository poemRepository;
    private final SectionRepositoryBase sectionRepositoryBase;
    private final ShortStoryRepository shortStoryRepository;
    private final AuthorRepository authorRepository;
    private final PlayRepository playRepository;

    @Autowired
    public LuceneConfig(PoemRepository poemRepository, SectionRepositoryBase sectionRepositoryBase,
                        ShortStoryRepository shortStoryRepository, AuthorRepository authorRepository,
                        PlayRepository playRepository) {
        this.poemRepository = poemRepository;
        this.sectionRepositoryBase = sectionRepositoryBase;
        this.shortStoryRepository = shortStoryRepository;
        this.authorRepository = authorRepository;
        this.playRepository = playRepository;
        this.init();
    }

    /**
     * Method defines the object and field specific analyzers.
     *
     * @return a map with custom field specific analysers.
     */
    private static Map<String, Analyzer> getAnalyzerMap() {
        Map<String, Analyzer> analyzerMap = new HashMap<>();

        try {
            Analyzer lowerCaseStandardAnalyzer = CustomAnalyzer.builder()
                    .withTokenizer("standard")
                    .addTokenFilter("lowercase")
                    .build();

            analyzerMap.put(SearchConstants.TEXT, CustomAnalyzer.builder()
                    .withTokenizer("standard")
                    .addTokenFilter("stop")
                    .addTokenFilter("englishminimalstem")
                    .addTokenFilter("englishpossessive")
                    .addTokenFilter("trim")
                    .addTokenFilter("lowercase")
                    .build());

            analyzerMap.put(SearchConstants.PARENT_TITLE, lowerCaseStandardAnalyzer);
            analyzerMap.put(SearchConstants.TITLE, lowerCaseStandardAnalyzer);
            analyzerMap.put(SearchConstants.AUTHOR_FIRST_NAME, lowerCaseStandardAnalyzer);
            analyzerMap.put(SearchConstants.AUTHOR_LAST_NAME, lowerCaseStandardAnalyzer);
            analyzerMap.put(SearchConstants.ACTOR_FIRST_NAME, lowerCaseStandardAnalyzer);
            analyzerMap.put(SearchConstants.ACTOR_MIDDLE_NAME, lowerCaseStandardAnalyzer);
            analyzerMap.put(SearchConstants.ACTOR_LAST_NAME, lowerCaseStandardAnalyzer);
            return analyzerMap;
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public static Analyzer getAnalyzer() {
        return new PerFieldAnalyzerWrapper(
                new EnglishAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET), getAnalyzerMap());
    }

    /**
     * Creates a field that stores TermVectors, Vector positions, and Vector offsets from a string. ONLY WORKS WITH
     * FIELDS NAMED 'TEXT.'
     *
     * @param text the text to add to the field.
     * @return a field with TermVectors, etc., added.
     */
    public static Field getTextField(final String text) {
        FieldType textField = new FieldType(TextField.TYPE_STORED);
        textField.setStoreTermVectors(true);
        textField.setStoreTermVectorPositions(true);
        textField.setStoreTermVectorOffsets(true);
        textField.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        return new Field(SearchConstants.TEXT, text, textField);
    }

    private void init() {
        this.indexPoems();
        this.indexSections();
        this.indexAuthors();
        this.indexShortStories();
        this.indexPlays();
    }

    /**
     * Writes all non-hidden poems to a com.sonnets.sonnet.search index. Delete any existing indexes first.
     */
    private void indexPoems() {
        LOGGER.debug("[SEARCH] :::::: Starting to write poem index...");
        List<Poem> poems = poemRepository.findAllByHidden(false).orElse(Collections.emptyList());
        LOGGER.debug(String.format("[SEARCH] :::::: Poems to index: %s.", poems.size()));

        int counter = 0;
        ArrayList<Document> documents = new ArrayList<>();
        for (Poem p : poems) {
            Document document = SearchRepository.parseCommonFields(new Document(), p);
            document.add(new TextField(SearchConstants.POEM_FORM, p.getForm(), Field.Store.YES));
            // :todo: this requires its own custom field.
            document.add(new TextField(SearchConstants.TOPIC_MODEL, String.valueOf(p.getTopicModel()),
                    Field.Store.YES));
            document.add(getTextField(String.join(SearchConstants.LINE_DELIMITER_POEM, p.getText())));
            documents.add(document);
            if (counter % 25 == 0) {
                LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", p.getId(),
                        p.getTitle()));
            }
            counter++;
        }
        SearchRepository.addDocuments(documents, TypeConstants.POEM);
        LOGGER.debug("[SEARCH] :::::: poems indexed successfully!");
    }

    /**
     * Writes all sections to a com.sonnets.sonnet.search index. Deletes any existing indexes first.
     */
    private void indexSections() {
        LOGGER.debug("[SEARCH] :::::: Starting to write section index...");
        List<Section> sections = sectionRepositoryBase.findAll();
        LOGGER.debug(String.format("[SEARCH] :::::: Sections to index: %s.", sections.size()));

        int counter = 0;
        ArrayList<Document> documents = new ArrayList<>();
        for (Section s : sections) {
            Document document = SearchRepository.parseCommonFields(new Document(), s);
            document.add(new StringField(SearchConstants.PARENT_ID, s.getParentId().toString(), Field.Store.YES));
            document.add(new TextField(SearchConstants.PARENT_TITLE, s.getParentTitle(), Field.Store.YES));
            document.add(getTextField(s.getText()));

            documents.add(document);
            if (counter % 25 == 0) {
                LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", s.getId(),
                        s.getTitle()));
            }
            counter++;
        }

        SearchRepository.addDocuments(documents, TypeConstants.SECTION);
        LOGGER.debug("[SEARCH] :::::: sections indexed successfully!");
    }

    private void indexShortStories() {
        LOGGER.debug("[SEARCH] :::::: Starting to write section index...");
        List<ShortStory> shortStories = shortStoryRepository.findAll();
        LOGGER.debug(String.format("[SEARCH] :::::: Short stories to index: %s.", shortStories.size()));

        int counter = 0;
        List<Document> documents = new ArrayList<>();
        for (ShortStory s : shortStories) {
            Document document = SearchRepository.parseCommonFields(new Document(), s);
            document.add(getTextField(s.getText()));
            documents.add(document);
            if (counter % 25 == 0) {
                LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", s.getId(),
                        s.getTitle()));
            }
            counter++;
        }
        SearchRepository.addDocuments(documents, TypeConstants.SHORT_STORY);
        LOGGER.debug("[SEARCH] :::::: Short stories indexed successfully!");
    }

    private void indexPlays() {
        LOGGER.debug("[SEARCH] :::::: Starting to write play index...");
        // Clear the play and dialog lines indexes manually.
        SearchRepository.clearIndex(TypeConstants.PLAY);
        SearchRepository.clearIndex(TypeConstants.DILI);
        List<Play> plays = playRepository.findAll();
        LOGGER.debug(String.format("[SEARCH] :::::: Plays to index: %s.", plays.size()));

        int counter = 0;
        for (Play p : plays) {
            PlayService.addSearchDocument(p);
            if (counter % 25 == 0) {
                LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", p.getId(),
                        p.getTitle()));
            }
            counter++;
        }
        LOGGER.debug("[SEARCH] :::::: Plays indexed successfully!");
    }

    private void indexAuthors() {
        LOGGER.debug("[SEARCH] :::::: Starting to write author index...");
        List<Author> authors = authorRepository.findAll();
        LOGGER.debug(String.format("[SEARCH] :::::: Authors to index: %s.", authors.size()));

        int counter = 0;
        List<Document> documents = new ArrayList<>();
        for (Author a : authors) {
            Document document = new Document();
            document.add(new StringField(SearchConstants.ID, String.valueOf(a.getId()), Field.Store.YES));
            document.add(new TextField(SearchConstants.AUTHOR_FIRST_NAME, a.getFirstName(), Field.Store.YES));
            document.add(new TextField(SearchConstants.AUTHOR_LAST_NAME, a.getLastName(), Field.Store.YES));
            documents.add(document);
            if (counter % 25 == 0) {
                LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", a.getId(),
                        a.getLastName()));
            }
            counter++;
        }

        SearchRepository.addDocuments(documents, TypeConstants.AUTHOR);
        LOGGER.debug("[SEARCH] :::::: Authors indexed successfully!");
    }
}
