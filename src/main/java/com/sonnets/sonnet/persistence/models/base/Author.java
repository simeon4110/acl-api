package com.sonnets.sonnet.persistence.models.base;

import com.google.gson.annotations.Expose;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Model for author objects. Authors are standalone, but are automatically embedded in all Item objects.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table
public class Author extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -590157884690722884L;
    @Id
    @DocumentId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Long id;
    @Field(name = SearchConstants.AUTHOR_FIRST_NAME_RAW, store = Store.YES, analyze = Analyze.YES,
            termVector = TermVector.YES)
    @Column
    @Expose
    private String firstName;
    @Field(name = SearchConstants.AUTHOR_MIDDLE_NAME_RAW, store = Store.YES, analyze = Analyze.YES,
            termVector = TermVector.YES)
    @Column
    @Expose
    private String middleName;
    @Field(name = SearchConstants.AUTHOR_LAST_NAME_RAW, store = Store.YES, analyze = Analyze.YES,
            termVector = TermVector.YES)
    @Column
    @Expose
    private String lastName;

    public Author() {
        super();
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id) &&
                Objects.equals(firstName, author.firstName) &&
                Objects.equals(middleName, author.middleName) &&
                Objects.equals(lastName, author.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, middleName, lastName);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
