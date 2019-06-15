package org.acl.database.persistence.models.theater;

import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Item;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A work of theater. Structure is Play -> Act -> Scene -> DialogLine/StageDirection
 *
 * @author Josh Harkema
 */
@Entity
@DiscriminatorValue(TypeConstants.PLAY)
public class Play extends Item implements Serializable {
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Act> acts;

    public Set<Act> getActs() {
        return acts;
    }

    public void setActs(Set<Act> acts) {
        this.acts = acts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Play play = (Play) o;
        return Objects.equals(acts, play.acts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), acts);
    }

    @Override
    public String toString() {
        return "Play{" +
                "acts=" + acts +
                '}';
    }
}
