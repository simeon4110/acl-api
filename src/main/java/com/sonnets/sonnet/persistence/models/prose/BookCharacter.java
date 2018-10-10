package com.sonnets.sonnet.persistence.models.prose;

import com.sonnets.sonnet.persistence.bridges.DialogBridge;
import com.sonnets.sonnet.persistence.models.annotation_types.Dialog;
import com.sonnets.sonnet.persistence.models.base.Auditable;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    @Field(name = "character_dialog", store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @FieldBridge(impl = DialogBridge.class)
    private Set<Dialog> dialog;

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

    public Set<Dialog> getDialog() {
        return dialog;
    }

    public void setDialog(Set<Dialog> dialog) {
        this.dialog = dialog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookCharacter that = (BookCharacter) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(description, that.description) &&
                Objects.equals(dialog, that.dialog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, gender, description, dialog);
    }

    @Override
    public String toString() {
        return "BookCharacter{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                ", dialog=" + dialog +
                "} " + super.toString();
    }
}
