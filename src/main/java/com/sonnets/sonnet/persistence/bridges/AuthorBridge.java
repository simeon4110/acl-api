package com.sonnets.sonnet.persistence.bridges;

import com.sonnets.sonnet.persistence.models.base.Author;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.Calendar;

public class AuthorBridge implements FieldBridge {
    @Override
    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        Author author = (Author) o;
        Calendar cal = Calendar.getInstance();
        cal.setTime(author.getBirthDate());
        String birthYear = String.valueOf(cal.get(Calendar.YEAR));
        cal.setTime(author.getDeathDate());
        String deathYear = String.valueOf(cal.get(Calendar.YEAR));

        luceneOptions.addFieldToDocument(s + ".firstName", author.getFirstName(), document);
        luceneOptions.addFieldToDocument(s + ".middleName", author.getMiddleName(), document);
        luceneOptions.addFieldToDocument(s + ".lastName", author.getLastName(), document);
        luceneOptions.addFieldToDocument(s + ".birthYear", birthYear, document);
        luceneOptions.addFieldToDocument(s + ".deathYear", deathYear, document);
    }
}
