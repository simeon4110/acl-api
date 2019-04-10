package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.config.LuceneConfig;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.base.Section;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * All search index related CRUD is defined here. Type specific index methods are also defined here, but I may move
 * them in the future.
 *
 * @author Josh Harkema
 */
@Service
public abstract class SearchCRUDService {
    private static final Logger LOGGER = Logger.getLogger(SearchCRUDService.class);

    /**
     * @param itemType the type of item to get a reader for.
     * @return an opened index reader.
     * @throws IOException if any pth related issues occur.
     */
    static IndexReader getReader(final String itemType) throws IOException {
        return DirectoryReader.open(FSDirectory.open(
                Paths.get(String.format("%s/%s", SearchConstants.DOCS_PATH, itemType))
        ));
    }

    /**
     * @param itemType TypeConstant of the item being indexed.
     * @return an opened IndexWriter.
     * @throws IOException if any path related issues occur.
     */
    private static IndexWriter getWriter(final String itemType) throws IOException {
        FSDirectory dir = FSDirectory.open(Paths.get(String.format("%s/%s", SearchConstants.DOCS_PATH, itemType)));
        IndexWriterConfig config = new IndexWriterConfig(LuceneConfig.getAnalyzer());
        return new IndexWriter(dir, config);
    }

    /**
     * @param docId   id of the doc to get.
     * @param docType type of the doc to get.
     * @return a document.
     */
    private static Document getLuceneDocument(final String docId, final String docType) {
        try (IndexReader reader = getReader(docType)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(new TermQuery(new Term(SearchConstants.ID, docId)), 1);
            if (docs.totalHits.value == 1) {
                return searcher.doc(docs.scoreDocs[0].doc);
            } else {
                reader.close();
                throw new RuntimeException("Document not found, or more than one document found.");
            }
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * @param docId    id of Document to update.
     * @param document Document with new info.
     * @param itemType item type of Document.
     */
    private static void updateDocument(final String docId, final Document document, final String itemType) {
        try (IndexWriter writer = getWriter(itemType)) {
            writer.updateDocument(new Term(SearchConstants.ID, docId), document);
            writer.commit();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Parses fields for all Objects that inherit the Item class.
     *
     * @param document the document to parse the fields onto.
     * @param item     the item containing the data.
     * @return the document with the Item fields added.
     */
    public static Document parseCommonFields(final Document document, final Item item) {
        document.add(new StringField(SearchConstants.ID, item.getId().toString(), Field.Store.YES));
        document.add(new TextField(SearchConstants.TITLE, item.getTitle(), Field.Store.YES));
        document.add(new TextField(SearchConstants.CATEGORY, item.getCategory(), Field.Store.YES));
        document.add(new TextField(SearchConstants.AUTHOR_FIRST_NAME, item.getAuthor().getFirstName(),
                Field.Store.YES));
        document.add(new TextField(SearchConstants.AUTHOR_LAST_NAME, item.getAuthor().getLastName(),
                Field.Store.YES));
        document.add(new StringField(SearchConstants.PERIOD, item.getPeriod(), Field.Store.YES));
        if (item.getPublicDomain() != null) { // Some items do not have this toggled.
            document.add(new TextField(SearchConstants.IS_PUBLIC, item.getPublicDomain().toString(),
                    Field.Store.YES));
        }
        return document;
    }

    /**
     * @param document Document to add.
     * @param itemType item type of document.
     */
    public static void addDocument(final Document document, final String itemType) {
        LOGGER.debug(String.format("[SEARCH] :::::: Adding document of type %s with title '%s'", itemType,
                document.get(SearchConstants.TITLE)));
        try (IndexWriter writer = getWriter(itemType)) {
            writer.addDocument(document);
            writer.commit();
            LOGGER.debug("[SEARCH] :::::: Document added!");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Add a list of documents to the lucene index.
     *
     * @param documents list of documents to add.
     * @param itemType  item type of documents.
     */
    public static void addDocuments(final Document[] documents, final String itemType) {
        LOGGER.debug(String.format("[SEARCH] :::::: Adding %s documents of type '%s'", documents.length, itemType));
        try (IndexWriter writer = getWriter(itemType)) {
            for (Document d : documents) {
                writer.addDocument(d);
            }
            writer.commit();
            LOGGER.debug("[SEARCH] :::::: Documents added!");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Add a list of documents to the lucene index. This method CLEARS THE WHOLE INDEX for itemType.
     *
     * @param documents  the documents to add.
     * @param itemType   the item type of the documents.
     * @param clearFirst must be true, method will fail otherwise.
     */
    public static void addDocuments(final List<Document> documents, final String itemType, final boolean clearFirst) {
        assert clearFirst; // Make sure clearFirst is true.
        LOGGER.debug(String.format("[SEARCH] :::::: Adding %s documents of type '%s'. Clearing indexes first",
                documents.size(), itemType));
        try (IndexWriter writer = getWriter(itemType)) {
            writer.deleteAll();
            for (Document d : documents) {
                writer.addDocument(d);
            }
            writer.commit();
            LOGGER.debug("[SEARCH] :::::: Documents added!");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static void deleteDocument(final String docId, final String itemType) {
        LOGGER.debug(String.format("[SEARCH] :::::: Deleting %s of type %s.", docId, itemType));
        try (IndexWriter writer = getWriter(itemType)) {
            writer.deleteDocuments(new Term(SearchConstants.ID, docId));
            writer.commit();
            writer.close();
            LOGGER.debug("[SEARCH] :::::: Item deleted!");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static void updatePoem(final Poem poem) {
        LOGGER.debug("[SEARCH] :::::: Updating poem: " + poem.getTitle());
        Document toUpdate = parseCommonFields(
                Objects.requireNonNull(getLuceneDocument(poem.getId().toString(), TypeConstants.POEM)), poem);
        toUpdate.add(new TextField(SearchConstants.POEM_FORM, poem.getForm(), Field.Store.YES));
        toUpdate.add(new TextField(SearchConstants.TOPIC_MODEL, String.valueOf(poem.getTopicModel()),
                Field.Store.YES));
        toUpdate.removeField(SearchConstants.TEXT);
        toUpdate.add(LuceneConfig.getTextField(String.join(" ", poem.getText())));
        updateDocument(poem.getId().toString(), toUpdate, TypeConstants.POEM);
        LOGGER.debug("[SEARCH] :::::: Poem updated!");
    }

    public static void updateSection(final Section section) {
        LOGGER.debug("[SEARCH] :::::: Updating section: " + section.getTitle());
        Document toUpdate = parseCommonFields(
                Objects.requireNonNull(getLuceneDocument(section.getId().toString(), TypeConstants.SECTION)), section);
        toUpdate.add(new StringField(SearchConstants.PARENT_ID, section.getParentId().toString(), Field.Store.YES));
        toUpdate.add(new TextField(SearchConstants.PARENT_TITLE, section.getParentTitle(), Field.Store.YES));
        toUpdate.add(LuceneConfig.getTextField(section.getText()));
        updateDocument(section.getId().toString(), toUpdate, TypeConstants.SECTION);
        LOGGER.debug("[SEARCH] :::::: Section updated!");
    }
}

