package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.prose.Section;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Bridge for indexing (lucene) embedded section objects.
 *
 * @author Josh Harkema
 */
public class SectionBridge implements FieldBridge {
    @Override
    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        Section section = (Section) o;

        luceneOptions.addFieldToDocument(s + ".title", section.getTitle(), document);
        luceneOptions.addFieldToDocument(s + ".text", section.getText(), document);
    }
}
