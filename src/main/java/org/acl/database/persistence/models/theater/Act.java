package org.acl.database.persistence.models.theater;

import org.acl.database.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * An "act" from a play. Basically a holder for scenes, but keeps track of any notes and the act number.
 *
 * @author Josh Harkema
 */
@Entity
@Table
public class Act extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private int number;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Scene> scenes;
    @Column
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Set<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(Set<Scene> scenes) {
        this.scenes = scenes;
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
        Act act = (Act) o;
        return number == act.number &&
                Objects.equals(id, act.id) &&
                Objects.equals(scenes, act.scenes) &&
                Objects.equals(notes, act.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, number, scenes, notes);
    }

    @Override
    public String toString() {
        return "Act{" +
                "id=" + id +
                ", number=" + number +
                ", scenes=" + scenes +
                ", notes='" + notes + '\'' +
                '}';
    }
}
