package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.base.Section;
import com.sonnets.sonnet.services.search.SearchConstants;
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
        @SuppressWarnings("unchecked") Collection<Section> sections = (Collection<Section>) o;

        for (Section section : sections) {
            luceneOptions.addFieldToDocument(section + SearchConstants.addDot(SearchConstants.TITLE),
                    section.getTitle(), document);
            luceneOptions.addFieldToDocument(section + SearchConstants.addDot(SearchConstants.TEXT),
                    section.getText(), document);
        }
    }
}
