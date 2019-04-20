package com.sonnets.sonnet.persistence.dtos.base;

import com.sonnets.sonnet.persistence.models.base.Author;

public class PoemOutDto {
    protected String period;
    private Long id;
    private Author author;
    private String title;
    private String sourceTitle;
    private String form;

    public PoemOutDto(Long id, Author author, String title, String sourceTitle, String period, String form) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.sourceTitle = sourceTitle;
        this.period = period;
        this.form = form;
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

    @Override
    public String toString() {
        return "PoemOutDto{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", period='" + period + '\'' +
                ", form='" + form + '\'' +
                '}';
    }
}
