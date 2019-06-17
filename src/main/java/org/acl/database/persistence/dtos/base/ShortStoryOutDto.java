package org.acl.database.persistence.dtos.base;

import org.acl.database.persistence.models.base.Author;

public class ShortStoryOutDto {
    private Long id;
    private Author author;
    private String title;
    private String sourceTitle;
    private String category;

    public ShortStoryOutDto(Long id, Author author, String title, String sourceTitle, String category) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.sourceTitle = sourceTitle;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "ShortStoryOutDto{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
