package org.acl.database.persistence.dtos.base;

import org.acl.database.persistence.models.base.Author;

import java.util.Date;

public class BookOutDto {
    private Long id;
    private Author author;
    private String title;
    private String type;
    private Date dateOfPublication;

    public BookOutDto(Long id, Author author, String title, String type, Date dateOfPublication) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.type = type;
        this.dateOfPublication = dateOfPublication;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(Date dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    @Override
    public String toString() {
        return "BookOutDto{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", dateOfPublication=" + dateOfPublication +
                '}';
    }
}
