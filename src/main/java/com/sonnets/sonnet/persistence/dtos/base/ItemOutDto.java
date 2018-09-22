package com.sonnets.sonnet.persistence.dtos.base;

import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This monstrosity is necessary to deal with the dynamic output of the corpora_items table. Do not judge me
 * by the failings of hibernate.
 *
 * @author Josh Harkema
 */
public class ItemOutDto {
    private BigDecimal id;
    private BigDecimal itemId;
    private String itemType;
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private String category;
    private String description;
    private String period;
    private String publicationStmt;
    private int publicationYear;
    private String sourceDesc;
    private String title;
    private Annotation annotation;
    private Confirmation confirmation;
    private BigDecimal parentId;
    private String bookType;
    private String bookTitle;
    private String text;
    private String poemText;
    private Author author;

    public ItemOutDto(final BigDecimal id, final BigDecimal itemId, final String itemType, final String createdBy,
                      final Date createdDate, final String lastModifiedBy, final Date lastModifiedDate,
                      final String category, final String description, final String period,
                      final String publicationStmt, final int publicationYear, final String sourceDesc,
                      final String title, final boolean confirmed, final Date confirmedAt,
                      final String confirmedBy, final boolean pendingRevision, final BigDecimal parentId,
                      final String bookTitle, final String text, final String poemText, final String firstName,
                      final String lastName, final String annotationBody, final String annotationCreatedBy,
                      final Date annotationCreatedDate, final String annotationLastModifiedBy,
                      final Date annotationLastModifiedDate) {
        this.id = id;
        this.itemId = itemId;
        this.itemType = itemType;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.category = category;
        this.description = description;
        this.period = period;
        this.publicationStmt = publicationStmt;
        this.publicationYear = publicationYear;
        this.sourceDesc = sourceDesc;
        this.title = title;
        this.confirmation = new Confirmation();
        this.confirmation.setConfirmed(confirmed);
        this.confirmation.setConfirmedAt(confirmedAt);
        this.confirmation.setConfirmedBy(confirmedBy);
        this.confirmation.setPendingRevision(pendingRevision);
        this.parentId = parentId;
        this.bookTitle = bookTitle;
        this.text = text;
        this.poemText = poemText;
        this.author = new Author();
        this.author.setFirstName(firstName);
        this.author.setLastName(lastName);
        this.annotation = new Annotation();
        this.annotation.setAnnotationBody(annotationBody);
        this.annotation.setCreatedBy(annotationCreatedBy);
        this.annotation.setCreatedDate(annotationCreatedDate);
        this.annotation.setLastModifiedBy(annotationLastModifiedBy);
        this.annotation.setLastModifiedDate(annotationLastModifiedDate);
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getItemId() {
        return itemId;
    }

    public void setItemId(BigDecimal itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getPublicationStmt() {
        return publicationStmt;
    }

    public void setPublicationStmt(String publicationStmt) {
        this.publicationStmt = publicationStmt;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public BigDecimal getParentId() {
        return parentId;
    }

    public void setParentId(BigDecimal parentId) {
        this.parentId = parentId;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPoemText() {
        return poemText;
    }

    public void setPoemText(String poemText) {
        this.poemText = poemText;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "ItemOutDto{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", itemType='" + itemType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", period='" + period + '\'' +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", publicationYear=" + publicationYear +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", title='" + title + '\'' +
                ", annotation=" + annotation +
                ", confirmation=" + confirmation +
                ", parentId=" + parentId +
                ", bookType='" + bookType + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", text='" + text + '\'' +
                ", poemText='" + poemText + '\'' +
                ", author=" + author +
                '}';
    }
}
