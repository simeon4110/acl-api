package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.services.search.SearchConstants;
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
        if (bookCharacters != null) {
            for (BookCharacter bookCharacter : bookCharacters) {
                luceneOptions.addFieldToDocument(s + SearchConstants.addDot(SearchConstants.BOOK_CHARACTER_FN),
                        bookCharacter.getFirstName(), document);
                luceneOptions.addFieldToDocument(s + SearchConstants.addDot(SearchConstants.BOOK_CHARACTER_LN),
                        bookCharacter.getLastName(), document);
                luceneOptions.addFieldToDocument(s + SearchConstants.addDot(SearchConstants.BOOK_CHARACTER_SEX),
                        bookCharacter.getGender(), document);
            }
        }
    }
}
