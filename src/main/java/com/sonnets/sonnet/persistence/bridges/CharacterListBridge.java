package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.Collection;

/**
 * Lucene bridge form mapping characters embedded in books.
 *
 * @author Josh Harkema
 */
public class CharacterListBridge implements FieldBridge {
    @Override
    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        Collection<BookCharacter> bookCharacters = (Collection<BookCharacter>) o;
        for (BookCharacter bookCharacter : bookCharacters) {
            luceneOptions.addFieldToDocument(s + ".character_first_name", bookCharacter.getFirstName(), document);
            luceneOptions.addFieldToDocument(s + ".character_last_name", bookCharacter.getLastName(), document);
            luceneOptions.addFieldToDocument(s + ".character_gender", bookCharacter.getGender(), document);
        }
    }
}
