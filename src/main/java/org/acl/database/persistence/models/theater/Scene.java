package org.acl.database.persistence.models.theater;

import org.acl.database.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A scene, holds dialog and stage directions (which must be manually sequenced as the data structure (set) is
 * unordered. This is intentional.
 *
 * @author Josh Harkema
 */
@Entity
@Table
public class Scene extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private int number;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<DialogLines> lines;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<StageDirection> directions;
    @Column
    private String notes;
    @Column
    private String title;

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

    public Set<DialogLines> getLines() {
        return lines;
    }

    public void setLines(Set<DialogLines> lines) {
        this.lines = lines;
    }

    public Set<StageDirection> getDirections() {
        return directions;
    }

    public void setDirections(Set<StageDirection> directions) {
        this.directions = directions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Scene scene = (Scene) o;
        return number == scene.number &&
                Objects.equals(id, scene.id) &&
                Objects.equals(lines, scene.lines) &&
                Objects.equals(directions, scene.directions) &&
                Objects.equals(notes, scene.notes) &&
                Objects.equals(title, scene.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, number, lines, directions, notes, title);
    }

    @Override
    public String toString() {
        return "Scene{" +
                "id=" + id +
                ", number=" + number +
                ", lines=" + lines +
                ", directions=" + directions +
                ", notes='" + notes + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
