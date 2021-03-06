package org.acl.database.persistence.models.base;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * This is the master POJO class, it holds all fields required for all works of literature in the database.
 *
 * @author Josh Harkema
 */
@MappedSuperclass
public abstract class Item extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -5596854181341354264L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String category;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Author author;
    @Column
    private String title;
    @Column
    private String sourceTitle;
    @Column
    private String description;
    @Column
    private String period;
    @Column
    private String edition;
    @Column
    private String placeOfPublication;
    @Column
    private String publisher;
    @Column
    private Date dateOfPublication;
    @Column
    private String shortTitle;
    @Column
    private String url;
    @Column
    private Date dateOfAccess;
    @Column
    private String journalName;
    @Column
    private String DOI;
    @Column
    private int journalVolume;
    @Column
    private int journalIssue;
    @Column
    private String journalPageRange;
    @Column
    private String journalAbbr;
    @Column
    private String language;
    @Column
    private Boolean isPublicDomain;
    @Column
    private String pageRange;

    public Item() {
        // Empty for spring data.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public Boolean getPublicDomain() {
        return isPublicDomain;
    }

    public void setPublicDomain(Boolean publicDomain) {
        isPublicDomain = publicDomain;
    }

    public String getPageRange() {
        return pageRange;
    }

    public void setPageRange(String pageRange) {
        this.pageRange = pageRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Item item = (Item) o;
        return journalVolume == item.journalVolume &&
                journalIssue == item.journalIssue &&
                Objects.equals(id, item.id) &&
                Objects.equals(category, item.category) &&
                Objects.equals(author, item.author) &&
                Objects.equals(title, item.title) &&
                Objects.equals(sourceTitle, item.sourceTitle) &&
                Objects.equals(description, item.description) &&
                Objects.equals(period, item.period) &&
                Objects.equals(edition, item.edition) &&
                Objects.equals(placeOfPublication, item.placeOfPublication) &&
                Objects.equals(publisher, item.publisher) &&
                Objects.equals(dateOfPublication, item.dateOfPublication) &&
                Objects.equals(shortTitle, item.shortTitle) &&
                Objects.equals(url, item.url) &&
                Objects.equals(dateOfAccess, item.dateOfAccess) &&
                Objects.equals(journalName, item.journalName) &&
                Objects.equals(DOI, item.DOI) &&
                Objects.equals(journalPageRange, item.journalPageRange) &&
                Objects.equals(journalAbbr, item.journalAbbr) &&
                Objects.equals(language, item.language) &&
                Objects.equals(isPublicDomain, item.isPublicDomain) &&
                Objects.equals(pageRange, item.pageRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, category, author, title, sourceTitle, description, period, edition,
                placeOfPublication, publisher, dateOfPublication, shortTitle, url, dateOfAccess, journalName, DOI,
                journalVolume, journalIssue, journalPageRange, journalAbbr, language, isPublicDomain, pageRange);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", description='" + description + '\'' +
                ", period='" + period + '\'' +
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
                ", isPublicDomain=" + isPublicDomain +
                ", pageRange='" + pageRange + '\'' +
                "} " + super.toString();
    }
}
