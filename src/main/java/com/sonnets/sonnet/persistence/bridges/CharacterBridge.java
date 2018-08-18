package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Lucene bridge form mapping characters embedded in books.
 *
 * @author Josh Harkema
 */
public class CharacterBridge implements FieldBridge {
    @Override
    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        BookCharacter bookCharacter = (BookCharacter) o;

        luceneOptions.addFieldToDocument(s + ".firstName", bookCharacter.getFirstName(), document);
        luceneOptions.addFieldToDocument(s + ".lastName", bookCharacter.getLastName(), document);
        luceneOptions.addFieldToDocument(s + ".gender", bookCharacter.getGender(), document);
    }
}