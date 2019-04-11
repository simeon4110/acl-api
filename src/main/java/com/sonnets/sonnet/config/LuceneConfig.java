package com.sonnets.sonnet.config;

import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.base.Section;
import com.sonnets.sonnet.persistence.repositories.RepositoryException;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.persistence.repositories.section.SectionRepositoryBase;
import com.sonnets.sonnet.services.search.SearchCRUDService;
import com.sonnets.sonnet.services.search.SearchConstants;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static PerFieldAnalyzerWrapper analyzer;

    @Autowired
    public LuceneConfig(PoemRepository poemRepository, SectionRepositoryBase sectionRepositoryBase) {
        this.poemRepository = poemRepository;
        this.sectionRepositoryBase = sectionRepositoryBase;
        analyzer = new PerFieldAnalyzerWrapper(
                new EnglishAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET), getAnalyzerMap());
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
            return analyzerMap;
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public static Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * Creates a field that stores TermVectors, Vector positions, and Vector offsets from a string. ONLY WORKS WITH
     * FIELDS NAMED TEXT.
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
    }

    /**
     * Writes all non-hidden poems to a search index. Delete any existing indexes first.
     */
    private void indexPoems() {
        LOGGER.debug("[SEARCH] :::::: Starting to write poem index...");
        List<Poem> poems = poemRepository.findAllByHidden(false).orElseThrow(RepositoryException::new);
        LOGGER.debug(String.format("[SEARCH] :::::: Poems to index: %s.", poems.size()));

        ArrayList<Document> documents = new ArrayList<>();
        for (Poem p : poems) {
            Document document = SearchCRUDService.parseCommonFields(new Document(), p);
            document.add(new TextField(SearchConstants.POEM_FORM, p.getForm(), Field.Store.YES));
            // :todo: this requires its own custom field.
            document.add(new TextField(SearchConstants.TOPIC_MODEL, String.valueOf(p.getTopicModel()),
                    Field.Store.YES));
            document.add(getTextField(String.join(SearchConstants.LINE_DELIMITER_POEM, p.getText())));
            documents.add(document);
            LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", p.getId(),
                    p.getTitle()));
        }

        SearchCRUDService.addDocuments(documents, TypeConstants.POEM, true);
        LOGGER.debug("[SEARCH] :::::: poems indexed successfully!");
    }

    /**
     * Writes all sections to a search index. Deletes any existing indexes first.
     */
    private void indexSections() {
        LOGGER.debug("[SEARCH] :::::: Starting to write section index...");
        List<Section> sections = sectionRepositoryBase.findAll();
        LOGGER.debug(String.format("[SEARCH] :::::: Sections to index: %s.", sections.size()));

        ArrayList<Document> documents = new ArrayList<>();
        for (Section s : sections) {
            Document document = SearchCRUDService.parseCommonFields(new Document(), s);
            document.add(new StringField(SearchConstants.PARENT_ID, s.getParentId().toString(), Field.Store.YES));
            document.add(new TextField(SearchConstants.PARENT_TITLE, s.getParentTitle(), Field.Store.YES));
            document.add(getTextField(s.getText()));

            documents.add(document);
            LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", s.getId(),
                    s.getTitle()));
        }

        SearchCRUDService.addDocuments(documents, TypeConstants.SECTION, true);
        LOGGER.debug("[SEARCH] :::::: sections indexed successfully!");
    }
}
