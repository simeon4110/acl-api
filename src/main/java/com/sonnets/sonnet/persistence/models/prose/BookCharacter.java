package com.sonnets.sonnet.persistence.models.prose;

import com.sonnets.sonnet.persistence.models.base.Auditable;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Stores characters. Not standalone, only instantiate as part of other item.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
public class BookCharacter extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -2343275086044899594L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Field(name = "character_first_name", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String firstName;
    @Field(name = "character_last_name", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String lastName;
    @Field(name = "character_gender", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String gender;
    @Column
    private String description;

    public BookCharacter() {
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookCharacter bookCharacter = (BookCharacter) o;
        return Objects.equals(id, bookCharacter.id) &&
                Objects.equals(firstName, bookCharacter.firstName) &&
                Objects.equals(lastName, bookCharacter.lastName) &&
                Objects.equals(gender, bookCharacter.gender) &&
                Objects.equals(description, bookCharacter.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, gender, description);
    }

    @Override
    public String toString() {
        return "BookCharacter{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
