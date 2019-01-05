package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.base.SourceDetails;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Bridge for mapping SourceDetails to Items for search functionality.
 *
 * @author Josh Harkema
 */
public class SourceDetailsBridge implements FieldBridge {
    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        SourceDetails details = (SourceDetails) value;
        luceneOptions.addFieldToDocument(name + ".edition", details.getEdition(), document);
        luceneOptions.addFieldToDocument(name + ".placeOfPublication", details.getPlaceOfPublication(),
                document);
        luceneOptions.addFieldToDocument(name + ".publisher", details.getPublisher(), document);
        luceneOptions.addFieldToDocument(name + ".dateOfPublication",
                details.getDateOfPublication().toString(), document);
        luceneOptions.addFieldToDocument(name + ".journalName", details.getJournalName(), document);
        luceneOptions.addFieldToDocument(name + ".DOI", details.getDOI(), document);
        luceneOptions.addFieldToDocument(name + ".journalVolume", String.valueOf(details.getJournalVolume()),
                document);
        luceneOptions.addFieldToDocument(name + ".journalIssue", String.valueOf(details.getJournalIssue()),
                document);
        luceneOptions.addFieldToDocument(name + ".journalPageRange", details.getJournalPageRange(), document);
        luceneOptions.addFieldToDocument(name + ".language", details.getLanguage(), document);
    }
}
