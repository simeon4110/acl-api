package com.sonnets.sonnet.persistence.dtos.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * This monstrosity is necessary to deal with the dynamic output of the corpora_items table. Do not judge me
 * by the failings of hibernate.
 *
 * @author Josh Harkema
 */
public class ItemOutDto implements Serializable {
    private static final long serialVersionUID = -5792420470777872258L;
    private BigDecimal corporaId;
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
    private boolean confirmed;
    private Date confirmedAt;
    private String confirmedBy;
    private boolean pendingRevision;
    private String parentId;
    private String bookType;
    private String text;
    private String firstName;
    private String lastName;
    private String bookTitle;
    private String poemText;

    public ItemOutDto() {
        // Empty by design.
    }

    public BigDecimal getCorporaId() {
        return corporaId;
    }

    public void setCorporaId(BigDecimal corporaId) {
        this.corporaId = corporaId;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public boolean isPendingRevision() {
        return pendingRevision;
    }

    public void setPendingRevision(boolean pendingRevision) {
        this.pendingRevision = pendingRevision;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getPoemText() {
        return poemText;
    }

    public void setPoemText(String poemText) {
        this.poemText = poemText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOutDto dto = (ItemOutDto) o;
        return publicationYear == dto.publicationYear &&
                confirmed == dto.confirmed &&
                pendingRevision == dto.pendingRevision &&
                Objects.equals(corporaId, dto.corporaId) &&
                Objects.equals(itemId, dto.itemId) &&
                Objects.equals(itemType, dto.itemType) &&
                Objects.equals(createdBy, dto.createdBy) &&
                Objects.equals(createdDate, dto.createdDate) &&
                Objects.equals(lastModifiedBy, dto.lastModifiedBy) &&
                Objects.equals(lastModifiedDate, dto.lastModifiedDate) &&
                Objects.equals(category, dto.category) &&
                Objects.equals(description, dto.description) &&
                Objects.equals(period, dto.period) &&
                Objects.equals(publicationStmt, dto.publicationStmt) &&
                Objects.equals(sourceDesc, dto.sourceDesc) &&
                Objects.equals(title, dto.title) &&
                Objects.equals(confirmedAt, dto.confirmedAt) &&
                Objects.equals(confirmedBy, dto.confirmedBy) &&
                Objects.equals(parentId, dto.parentId) &&
                Objects.equals(bookType, dto.bookType) &&
                Objects.equals(text, dto.text) &&
                Objects.equals(firstName, dto.firstName) &&
                Objects.equals(lastName, dto.lastName) &&
                Objects.equals(bookTitle, dto.bookTitle) &&
                Objects.equals(poemText, dto.poemText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corporaId, itemId, itemType, createdBy, createdDate, lastModifiedBy, lastModifiedDate,
                category, description, period, publicationStmt, publicationYear, sourceDesc, title, confirmed,
                confirmedAt, confirmedBy, pendingRevision, parentId, bookType, text, firstName, lastName, bookTitle,
                poemText);
    }

    @Override
    public String toString() {
        return "ItemOutDto{" +
                "corporaId=" + corporaId +
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
                ", confirmed=" + confirmed +
                ", confirmedAt=" + confirmedAt +
                ", confirmedBy='" + confirmedBy + '\'' +
                ", pendingRevision=" + pendingRevision +
                ", parentId='" + parentId + '\'' +
                ", bookType='" + bookType + '\'' +
                ", text='" + text + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", poemText='" + poemText + '\'' +
                '}';
    }
}
