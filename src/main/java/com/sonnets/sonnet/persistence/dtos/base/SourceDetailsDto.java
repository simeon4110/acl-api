package com.sonnets.sonnet.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public abstract class SourceDetailsDto {
    private Long id;
    private String edition;
    @NotEmpty
    private String placeOfPublication;
    @NotEmpty
    private String publisher;
    @NotEmpty
    private Date dateOfPublication;
    private String shortTitle;
    private String url;
    private Date dateOfAccess;
    private String journalName;
    private String DOI;
    private int journalVolume;
    private int journalIssue;
    private String journalPageRange;
    private String journalAbbr;
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

    @SuppressWarnings("Duplicates")
    @Override
    public String toString() {
        return "SourceDetailsDto{" +
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
                '}';
    }
}
