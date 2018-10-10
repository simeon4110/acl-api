package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.base.Author;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Bridge for indexing (lucene) embedded author objects.
 *
 * @author Josh Harkema
 */
public class AuthorBridge implements FieldBridge {
    @Override
    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        Author author = (Author) o;
        luceneOptions.addFieldToDocument(s + ".firstName", author.getFirstName(), document);
        luceneOptions.addFieldToDocument(s + ".lastName", author.getLastName(), document);
    }
}
