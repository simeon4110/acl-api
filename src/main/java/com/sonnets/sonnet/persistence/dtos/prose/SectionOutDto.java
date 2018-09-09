package com.sonnets.sonnet.persistence.dtos.prose;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.prose.Book;

import java.math.BigDecimal;

@JsonSerialize
public class SectionOutDto {
    private BigDecimal id;
    private String category;
    private String description;
    private String period;
    private String publicationStmt;
    private Integer publicationYear;
    private String sourceDesc;
    private String title;
    private Confirmation confirmation;
    private Author author;
    private Book book;
    private String text;

    public SectionOutDto(BigDecimal id, String category, String description, String period, String publicationStmt,
                         Integer publicationYear, String sourceDesc, String title, boolean confirmed,
                         java.util.Date confirmedAt, String confirmedBy, boolean pendingRevision, BigDecimal authorId,
                         String firstName, String lastName, BigDecimal parentId, String bookTitle, String bookType,
                         String text) {
        this.id = id;
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
        this.author = new Author();
        this.author.setId(authorId.longValue());
        this.author.setFirstName(firstName);
        this.author.setLastName(lastName);
        this.book = new Book();
        this.book.setId(parentId.longValue());
        this.book.setTitle(bookTitle);
        this.book.setType(bookType);
        this.text = text;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
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

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
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

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "SectionOutDto{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", period='" + period + '\'' +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", publicationYear=" + publicationYear +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", title='" + title + '\'' +
                ", confirmation=" + confirmation +
                ", author=" + author +
                ", book=" + book +
                ", text='" + text + '\'' +
                '}';
    }
}
