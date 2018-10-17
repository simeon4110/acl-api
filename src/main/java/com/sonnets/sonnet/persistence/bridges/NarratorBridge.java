package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Parses a narrator (BookCharacter) field into a lucene document.
 *
 * @author Josh Harkema
 */
public class NarratorBridge implements FieldBridge {
    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        BookCharacter narrator = (BookCharacter) value;
        if (narrator != null) {
            luceneOptions.addFieldToDocument(name + ".character_first_name", narrator.getFirstName(),
                    document);
            luceneOptions.addFieldToDocument(name + ".character_last_name", narrator.getLastName(), document);
            luceneOptions.addFieldToDocument(name + ".character_gender", narrator.getGender(), document);
        }
    }
}
