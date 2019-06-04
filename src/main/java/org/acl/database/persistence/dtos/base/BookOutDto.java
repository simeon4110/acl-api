package org.acl.database.persistence.dtos.base;

import org.acl.database.persistence.models.base.Author;

public class BookOutDto {
    private Long id;
    private Author author;
    private String title;

    public BookOutDto(Long id, Author author, String title) {
        this.id = id;
        this.author = author;
        this.title = title;
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

    @Override
    public String toString() {
        return "BookOutDto{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
                '}';
    }
}
