package com.sonnets.sonnet.persistence.dtos.poetry;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This POJO is the result of nearly 10 hours of figuring out that SQL doesn't map the same way as MySQL. I
 * now feel like I've learned how far M$ is willing to go to prevent interoperability.
 *
 * @author Josh Harkema
 */
@JsonSerialize
public class PoemOutDto implements Serializable {
    private static final long serialVersionUID = -114794321718486288L;
    private BigDecimal id;
    private String title;
    private String category;
    private String description;
    private Integer publicationYear;
    private String publicationStmt;
    private String sourceDesc;
    private String period;
    private String form;
    private Confirmation confirmation;
    private Author author;
    private String text;

    public PoemOutDto(BigDecimal id, String title, String category, String description, Integer publicationYear,
                      String publicationStmt, String sourceDesc, String period, String form, boolean confirmed,
                      java.util.Date confirmedAt, String confirmedBy, boolean pendingRevision, BigDecimal authorId,
                      String firstName, String lastName, String text) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.publicationYear = publicationYear;
        this.publicationStmt = publicationStmt;
        this.sourceDesc = sourceDesc;
        this.period = period;
        this.form = form;
        this.confirmation = new Confirmation();
        this.confirmation.setConfirmed(confirmed);
        this.confirmation.setConfirmedAt(confirmedAt);
        this.confirmation.setConfirmedBy(confirmedBy);
        this.confirmation.setPendingRevision(pendingRevision);
        this.author = new Author();
        this.author.setId(authorId.longValue());
        this.author.setFirstName(firstName);
        this.author.setLastName(lastName);
        this.text = text;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPublicationStmt() {
        return publicationStmt;
    }

    public void setPublicationStmt(String publicationStmt) {
        this.publicationStmt = publicationStmt;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "PoemOutDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", period='" + period + '\'' +
                ", form='" + form + '\'' +
                ", confirmation=" + confirmation +
                ", author=" + author +
                ", text='" + text + '\'' +
                '}';
    }
}
