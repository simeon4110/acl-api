package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.annotation_types.Dialog;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.Collection;

/**
 * Parses collections of Dialog objects into lucene documents.
 *
 * @author Josh Harkema
 */
public class DialogBridge implements FieldBridge {
    @Override
    @SuppressWarnings("unchecked")
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        Collection<Dialog> dialogs = (Collection<Dialog>) value;
        for (Dialog d : dialogs) {
            luceneOptions.addFieldToDocument(name + ".body", d.getBody(), document);
        }
    }
}
