package org.acl.database.persistence.models.theater;

import org.acl.database.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Unlike dialog, this object represents something an actor or actors "does" rather than "says." These can be attached
 * to multiple actors.
 *
 * @author Josh Harkema.
 */
@Entity
@Table
public class StageDirection extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String body;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "direction_actors",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "stage_direction_id"))
    private Set<Actor> actors;
    @Column
    private int sequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StageDirection that = (StageDirection) o;
        return sequence == that.sequence &&
                Objects.equals(id, that.id) &&
                Objects.equals(body, that.body) &&
                Objects.equals(actors, that.actors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, body, actors, sequence);
    }

    @Override
    public String toString() {
        return "StageDirection{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", actors=" + actors +
                ", sequence=" + sequence +
                '}';
    }
}
