package org.acl.database.persistence.dtos.base;

import org.acl.database.persistence.models.base.Author;

import java.io.Serializable;

public class PoemOutDto implements Serializable {
    private Long id;
    private Author author;
    private String title;
    private String sourceTitle;
    private String period;
    private String form;
    private String category;

    public PoemOutDto(Long id, Author author, String title, String sourceTitle, String period, String form,
                      String category) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.sourceTitle = sourceTitle;
        this.period = period;
        this.form = form;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "PoemOutDto{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", period='" + period + '\'' +
                ", form='" + form + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
