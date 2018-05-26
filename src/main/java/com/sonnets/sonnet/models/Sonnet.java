package com.sonnets.sonnet.models;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Model to store the sonnet info in MySQL.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table(name = "sonnets")
public class Sonnet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Field(name = "firstName")
    @Column
    private String firstName;
    @Field(name = "lastName")
    @Column
    private String lastName;
    @Field(name = "title")
    @Column
    private String title;
    @Field(name = "publicationYear")
    @Column
    private String publicationYear;
    @Column
    private String publicationStmt;
    @Column
    private String sourceDesc;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
    @Column
    @IndexedEmbedded
    @Field(name = "text")
    @ElementCollection
    private List<String> text;

    /**
     * Default constructor generates a timestamp.
     */
    public Sonnet() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Sonnet(SonnetDto sonnetDto) {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.firstName = sonnetDto.getFirstName();
        this.lastName = sonnetDto.getLastName();
        this.title = sonnetDto.getTitle();
        this.publicationYear = sonnetDto.getPublicationYear();
        this.publicationStmt = sonnetDto.getPublicationStmt();
        this.sourceDesc = sonnetDto.getSourceDesc();
        this.text = parseText(sonnetDto.getText().split("\\r?\\n"));
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getText() {
        return text;
    }

    /**
     * Parses text input into an Array for database storage.
     *
     * @param text a string[] of the text.
     * @return an ArrayList of the string[].
     */
    private static List<String> parseText(String[] text) {
        List<String> strings = new ArrayList<>();
        Collections.addAll(strings, text);

        return strings;
    }

    /**
     * Update an existing sonnet from a SonnetDto object.
     * @param sonnetDto the SonnetDto with the new data.
     * @return the updated Sonnet object.
     */
    public Sonnet update(SonnetDto sonnetDto) {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.firstName = sonnetDto.getFirstName();
        this.lastName = sonnetDto.getLastName();
        this.title = sonnetDto.getTitle();
        this.publicationYear = sonnetDto.getPublicationYear();
        this.publicationStmt = sonnetDto.getPublicationStmt();
        this.sourceDesc = sonnetDto.getSourceDesc();
        this.text = parseText(sonnetDto.getText().split("\\r?\\n"));

        return this;
    }

    /**
     * This parses a Sonnet so it shows "pretty" in html <textarea></textarea> elements. (adds \n for newlines.)
     * @return a nicely formatted string.
     */
    public String getTextPretty() {
        StringBuilder sb = new StringBuilder();
        for (String s : text) {
            sb.append(s).append("\n");
        }

        return sb.toString();
    }

    public void setText(List<String> text) {
        this.text = text;
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
        if (publicationStmt != null ? !publicationStmt.equals(sonnet.publicationStmt) : sonnet.publicationStmt != null)
            return false;
        if (sourceDesc != null ? !sourceDesc.equals(sonnet.sourceDesc) : sonnet.sourceDesc != null) return false;
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
        result = 31 * result + (publicationStmt != null ? publicationStmt.hashCode() : 0);
        result = 31 * result + (sourceDesc != null ? sourceDesc.hashCode() : 0);
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
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", updatedAt=" + updatedAt +
                ", text=" + text +
                '}';
    }

}
