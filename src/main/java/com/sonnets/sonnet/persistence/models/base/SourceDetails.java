package com.sonnets.sonnet.persistence.models.base;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Deals with all MLA related details.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
public class SourceDetails extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -8071521222922054352L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Field(name = "edition", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String edition;
    @Field(name = "placeOfPublication", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String placeOfPublication;
    @Field(name = "publisher", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String publisher;
    @Field(name = "dateOfPublication", store = Store.YES, analyze = Analyze.NO)
    @Column
    private Date dateOfPublication;
    @Column
    private String shortTitle;
    @Column
    private String url;
    @Column
    private Date dateOfAccess;
    @Field(name = "journalName", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String journalName;
    @Field(name = "DOI", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String DOI;
    @Field(name = "journalVolume", store = Store.YES, analyze = Analyze.NO)
    @Column
    private int journalVolume;
    @Field(name = "journalIssue", store = Store.YES, analyze = Analyze.NO)
    @Column
    private int journalIssue;
    @Field(name = "journalPageRange", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String journalPageRange;
    @Column
    private String journalAbbr;
    @Field(name = "language", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String language;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPlaceOfPublication() {
        return placeOfPublication;
    }

    public void setPlaceOfPublication(String placeOfPublication) {
        this.placeOfPublication = placeOfPublication;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(Date dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateOfAccess() {
        return dateOfAccess;
    }

    public void setDateOfAccess(Date dateOfAccess) {
        this.dateOfAccess = dateOfAccess;
    }

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public String getDOI() {
        return DOI;
    }

    public void setDOI(String DOI) {
        this.DOI = DOI;
    }

    public int getJournalVolume() {
        return journalVolume;
    }

    public void setJournalVolume(int journalVolume) {
        this.journalVolume = journalVolume;
    }

    public int getJournalIssue() {
        return journalIssue;
    }

    public void setJournalIssue(int journalIssue) {
        this.journalIssue = journalIssue;
    }

    public String getJournalPageRange() {
        return journalPageRange;
    }

    public void setJournalPageRange(String journalPageRange) {
        this.journalPageRange = journalPageRange;
    }

    public String getJournalAbbr() {
        return journalAbbr;
    }

    public void setJournalAbbr(String journalAbbr) {
        this.journalAbbr = journalAbbr;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SourceDetails details = (SourceDetails) o;
        return journalVolume == details.journalVolume &&
                journalIssue == details.journalIssue &&
                Objects.equals(id, details.id) &&
                Objects.equals(edition, details.edition) &&
                Objects.equals(placeOfPublication, details.placeOfPublication) &&
                Objects.equals(publisher, details.publisher) &&
                Objects.equals(dateOfPublication, details.dateOfPublication) &&
                Objects.equals(shortTitle, details.shortTitle) &&
                Objects.equals(url, details.url) &&
                Objects.equals(dateOfAccess, details.dateOfAccess) &&
                Objects.equals(journalName, details.journalName) &&
                Objects.equals(DOI, details.DOI) &&
                Objects.equals(journalPageRange, details.journalPageRange) &&
                Objects.equals(journalAbbr, details.journalAbbr) &&
                Objects.equals(language, details.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, edition, placeOfPublication, publisher, dateOfPublication, shortTitle,
                url, dateOfAccess, journalName, DOI, journalVolume, journalIssue, journalPageRange, journalAbbr,
                language);
    }

    @Override
    public String toString() {
        return "SourceDetails{" +
                "id=" + id +
                ", edition='" + edition + '\'' +
                ", placeOfPublication='" + placeOfPublication + '\'' +
                ", publisher='" + publisher + '\'' +
                ", dateOfPublication=" + dateOfPublication +
                ", shortTitle='" + shortTitle + '\'' +
                ", url='" + url + '\'' +
                ", dateOfAccess=" + dateOfAccess +
                ", journalName='" + journalName + '\'' +
                ", DOI='" + DOI + '\'' +
                ", journalVolume=" + journalVolume +
                ", journalIssue=" + journalIssue +
                ", journalPageRange='" + journalPageRange + '\'' +
                ", journalAbbr='" + journalAbbr + '\'' +
                ", language='" + language + '\'' +
                "} " + super.toString();
    }
}
