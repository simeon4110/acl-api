package com.sonnets.sonnet.models;

import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Model to store the sonnet info in MySQL
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "sonnets")
public class Sonnet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String title;
    @Column
    private String publicationYear;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
    @Column
    @ElementCollection(targetClass = String.class)
    private List<String> text;

    /**
     * Default constructor generates a timestamp.
     */
    public Sonnet() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public int countNumberOfLines() {
        return this.text.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sonnet sonnet = (Sonnet) o;

        if (id != null ? !id.equals(sonnet.id) : sonnet.id != null) return false;
        if (firstName != null ? !firstName.equals(sonnet.firstName) : sonnet.firstName != null) return false;
        if (lastName != null ? !lastName.equals(sonnet.lastName) : sonnet.lastName != null) return false;
        if (title != null ? !title.equals(sonnet.title) : sonnet.title != null) return false;
        if (publicationYear != null ? !publicationYear.equals(sonnet.publicationYear) : sonnet.publicationYear != null)
            return false;
        if (updatedAt != null ? !updatedAt.equals(sonnet.updatedAt) : sonnet.updatedAt != null) return false;
        return text != null ? text.equals(sonnet.text) : sonnet.text == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (publicationYear != null ? publicationYear.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Sonnet{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", publicationYear='" + publicationYear + '\'' +
                ", updatedAt=" + updatedAt +
                ", text=" + text +
                '}';
    }

}
