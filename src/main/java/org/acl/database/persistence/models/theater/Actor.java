package org.acl.database.persistence.models.theater;

import org.acl.database.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * An actor (or character) for a play.
 *
 * @author Josh Harkema
 */
@Entity
@Table
public class Actor extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String firstName;
    @Column
    private String middleName;
    @Column
    private String lastName;
    @Column
    private String notes;

    public Actor() {
        // Empty by design
    }

    public Actor(String firstName, String middleName, String lastName, String notes) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Actor actor = (Actor) o;
        return Objects.equals(id, actor.id) &&
                Objects.equals(firstName, actor.firstName) &&
                Objects.equals(middleName, actor.middleName) &&
                Objects.equals(lastName, actor.lastName) &&
                Objects.equals(notes, actor.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, middleName, lastName, notes);
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
