package com.sonnets.sonnet.config;

import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.repositories.RepositoryException;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
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

    @Autowired
    public LuceneConfig(PoemRepository poemRepository) {
        this.poemRepository = poemRepository;
        this.indexPoems();
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

    private void indexPoems() {
        LOGGER.debug("[SEARCH] :::::: Starting to write poem index...");
        List<Poem> poems = poemRepository.findAllByHidden(false).orElseThrow(RepositoryException::new);
        LOGGER.debug(String.format("[SEARCH] :::::: Poems to index: %s.", poems.size()));

        try (IndexWriter writer = createWriter(TypeConstants.POEM)) {
            for (Poem p : poems) {
                Document document = new Document();
                document.add(new StringField(SearchConstants.ID, p.getId().toString(), Field.Store.YES));
                document.add(new TextField(SearchConstants.TITLE, p.getTitle(), Field.Store.YES));
                document.add(new TextField(SearchConstants.CATEGORY, p.getCategory(), Field.Store.YES));
                document.add(new TextField(SearchConstants.AUTHOR_FIRST_NAME, p.getAuthor().getFirstName(),
                        Field.Store.YES));
                document.add(new TextField(SearchConstants.AUTHOR_LAST_NAME, p.getAuthor().getLastName(),
                        Field.Store.YES));
                document.add(new TextField(SearchConstants.PERIOD, p.getPeriod(), Field.Store.YES));
                document.add(new TextField(SearchConstants.IS_PUBLIC, p.getPublicDomain().toString(),
                        Field.Store.YES));
                document.add(new TextField(SearchConstants.POEM_FORM, p.getForm(), Field.Store.YES));
                // :todo: this requires its own custom field.
                document.add(new TextField(SearchConstants.TOPIC_MODEL, String.valueOf(p.getTopicModel()),
                        Field.Store.YES));

                // Parse poem text into custom field that stores term vectors.
                String textOut = String.join(" ", p.getText());
                FieldType poemTextField = new FieldType(TextField.TYPE_STORED);
                poemTextField.setStoreTermVectors(true);
                poemTextField.setStoreTermVectorPositions(true);
                poemTextField.setStoreTermVectorOffsets(true);
                poemTextField.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
                document.add(new Field(SearchConstants.TEXT, textOut, poemTextField));

                // add poem doc to writer.
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
}
