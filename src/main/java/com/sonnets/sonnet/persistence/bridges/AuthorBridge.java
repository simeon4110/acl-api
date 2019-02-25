package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Bridge for indexing (lucene) embedded author objects. Stores author details as author.firstName and author.lastName.
 *
 * @author Josh Harkema
 */
public class AuthorBridge implements FieldBridge {
    @Override
    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        Author author = (Author) o;
        document.add(new StringField(SearchConstants.AUTHOR_FIRST_NAME, author.getFirstName().toLowerCase(),
                Field.Store.YES));
        document.add(new StringField(SearchConstants.AUTHOR_LAST_NAME, author.getLastName().toLowerCase(),
                Field.Store.YES));
    }
}
