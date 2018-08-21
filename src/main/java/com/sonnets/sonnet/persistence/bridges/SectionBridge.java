package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.prose.Section;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.Collection;

/**
 * Bridge for indexing (lucene) embedded section objects.
 *
 * @author Josh Harkema
 */
public class SectionBridge implements FieldBridge {
    @Override
    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        Collection<Section> sections = (Collection<Section>) o;

        for (Section section : sections) {
            luceneOptions.addFieldToDocument(section + ".title", section.getTitle(), document);
            luceneOptions.addFieldToDocument(section + ".text", section.getText(), document);
        }
    }
}
