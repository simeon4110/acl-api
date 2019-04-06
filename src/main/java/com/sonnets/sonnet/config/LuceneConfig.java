package com.sonnets.sonnet.config;

import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.base.Section;
import com.sonnets.sonnet.persistence.repositories.RepositoryException;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.persistence.repositories.section.SectionRepositoryBase;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * This is where the Lucene indexes are defined. Runs after db initialization and writes Lucene indexes to
 * INDEX_DIR defined in SearchConstants.
 *
 * @author Josh Harkema
 */
@Configuration
public class LuceneConfig {
    private static final Logger LOGGER = Logger.getLogger(LuceneConfig.class);
    private final PoemRepository poemRepository;
    private final SectionRepositoryBase sectionRepositoryBase;

    @Autowired
    public LuceneConfig(PoemRepository poemRepository, SectionRepositoryBase sectionRepositoryBase) {
        this.poemRepository = poemRepository;
        this.sectionRepositoryBase = sectionRepositoryBase;
        this.init();
    }

    private void init() {
        this.indexPoems();
        this.indexSections();
    }

    /**
     * Returns an open index writer to INDEX_DIR/{objectType}.
     *
     * @param objectType TypeConstant of the item being indexed.
     * @return an opened IndexWriter.
     * @throws IOException if any path related issues occur.
     */
    private static IndexWriter createWriter(final String objectType) throws IOException {
        FSDirectory dir = FSDirectory.open(Paths.get(String.format("%s/%s", SearchConstants.DOCS_PATH, objectType)));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        return new IndexWriter(dir, config);
    }

    /**
     * Parses fields for all Objects that inherit the Item class.
     *
     * @param document the document to parse the fields onto.
     * @param item     the item containing the data.
     * @return the document with the Item fields added.
     */
    private Document parseCommonFields(final Document document, final Item item) {
        document.add(new StringField(SearchConstants.ID, item.getId().toString(), Field.Store.YES));
        document.add(new TextField(SearchConstants.TITLE, item.getTitle(), Field.Store.YES));
        document.add(new TextField(SearchConstants.CATEGORY, item.getCategory(), Field.Store.YES));
        document.add(new StringField(SearchConstants.AUTHOR_FIRST_NAME, item.getAuthor().getFirstName(),
                Field.Store.YES));
        document.add(new StringField(SearchConstants.AUTHOR_LAST_NAME, item.getAuthor().getLastName(),
                Field.Store.YES));
        document.add(new StringField(SearchConstants.PERIOD, item.getPeriod(), Field.Store.YES));
        if (item.getPublicDomain() != null) { // Some items do not have this toggled.
            document.add(new TextField(SearchConstants.IS_PUBLIC, item.getPublicDomain().toString(),
                    Field.Store.YES));
        }
        return document;
    }

    /**
     * Creates a field that stores TermVectors, Vector positions, and Vector offsets from a string. ONLY WORKS WITH
     * FIELDS NAMED TEXT.
     *
     * @param text the text to add to the field.
     * @return a field with TermVectors, etc., added.
     */
    private Field getTextField(final String text) {
        FieldType textField = new FieldType(TextField.TYPE_STORED);
        textField.setStoreTermVectors(true);
        textField.setStoreTermVectorPositions(true);
        textField.setStoreTermVectorOffsets(true);
        textField.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        return new Field(SearchConstants.TEXT, text, textField);
    }

    /**
     * Writes all non-hidden poems to a search index. Delete any existing indexes first.
     */
    private void indexPoems() {
        LOGGER.debug("[SEARCH] :::::: Starting to write poem index...");
        List<Poem> poems = poemRepository.findAllByHidden(false).orElseThrow(RepositoryException::new);
        LOGGER.debug(String.format("[SEARCH] :::::: Poems to index: %s.", poems.size()));

        try (IndexWriter writer = createWriter(TypeConstants.POEM)) {
            writer.deleteAll();
            for (Poem p : poems) {
                Document document = parseCommonFields(new Document(), p);
                document.add(new TextField(SearchConstants.POEM_FORM, p.getForm(), Field.Store.YES));
                // :todo: this requires its own custom field.
                document.add(new TextField(SearchConstants.TOPIC_MODEL, String.valueOf(p.getTopicModel()),
                        Field.Store.YES));
                document.add(getTextField(String.join(" ", p.getText())));

                writer.addDocument(document);
                LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", p.getId(),
                        p.getTitle()));
            }

            writer.commit();
            LOGGER.debug("[SEARCH] :::::: poems indexed successfully!");
        } catch (IOException e) {
            LOGGER.error("[SEARCH] :::::: Error writing to index for Poems...");
            LOGGER.error(e);
        }
    }

    /**
     * Writes all sections to a search index. Deletes any existing indexes first.
     */
    private void indexSections() {
        LOGGER.debug("[SEARCH] :::::: Starting to write section index...");
        List<Section> sections = sectionRepositoryBase.findAll();
        LOGGER.debug(String.format("[SEARCH] :::::: Sections to index: %s.", sections.size()));

        try (IndexWriter writer = createWriter(TypeConstants.SECTION)) {
            writer.deleteAll();
            for (Section s : sections) {
                Document document = parseCommonFields(new Document(), s);
                document.add(new StringField(SearchConstants.PARENT_ID, s.getParentId().toString(), Field.Store.YES));
                document.add(new StringField(SearchConstants.PARENT_TITLE, s.getParentTitle(), Field.Store.YES));
                document.add(getTextField(s.getText()));

                writer.addDocument(document);
                LOGGER.debug(String.format("[SEARCH] :::::: id: %s | '%s' added to document index.", s.getId(),
                        s.getTitle()));
            }

            writer.commit();
            LOGGER.debug("[SEARCH] :::::: sections indexed successfully!");
        } catch (IOException e) {
            LOGGER.error("[SEARCH] :::::: Error writing to index for sections...");
            LOGGER.error(e);
        }
    }
}
