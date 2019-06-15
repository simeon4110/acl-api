package org.acl.database.persistence.models.theater;

import org.acl.database.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A single piece of dialog (i.e. something an actor "says.") Must be sequenced. Can only attach a single author.
 *
 * @author Josh Harkema
 */
@Entity
@Table
public class DialogLines extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "actor_id")
    private Actor actor;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> body;
    @Column
    private int sequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
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
        DialogLines dialogLines = (DialogLines) o;
        return sequence == dialogLines.sequence &&
                Objects.equals(id, dialogLines.id) &&
                Objects.equals(actor, dialogLines.actor) &&
                Objects.equals(body, dialogLines.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, actor, body, sequence);
    }

    @Override
    public String toString() {
        return "DialogLines{" +
                "id=" + id +
                ", actor=" + actor +
                ", body='" + body + '\'' +
                ", sequence=" + sequence +
                '}';
    }
}
