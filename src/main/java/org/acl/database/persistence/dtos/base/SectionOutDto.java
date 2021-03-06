package org.acl.database.persistence.dtos.base;

import org.acl.database.persistence.models.base.Author;

public class SectionOutDto {
    private Long id;
    private Author author;
    private String title;
    private String parentTitle;
    private Long parentId;
    private String category;

    public SectionOutDto(Long id, Author author, String title, String parentTitle, Long parentId, String category) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.parentTitle = parentTitle;
        this.parentId = parentId;
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

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "SectionOutDto{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", parentTitle='" + parentTitle + '\'' +
                ", parentId=" + parentId +
                ", category='" + category + '\'' +
                '}';
    }
}
