package com.sonnets.sonnet.persistence.models.annotation;

import com.sonnets.sonnet.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract class for top-level annotation data.
 *
 * @author Josh Harkema
 */
@MappedSuperclass
abstract class AnnotationBase extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = 3250723133420178379L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long characterOffsetBegin;
    @Column
    private Long characterOffsetEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCharacterOffsetBegin() {
        return characterOffsetBegin;
    }

    public void setCharacterOffsetBegin(Long characterOffsetBegin) {
        this.characterOffsetBegin = characterOffsetBegin;
    }

    public Long getCharacterOffsetEnd() {
        return characterOffsetEnd;
    }

    public void setCharacterOffsetEnd(Long characterOffsetEnd) {
        this.characterOffsetEnd = characterOffsetEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnnotationBase that = (AnnotationBase) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(characterOffsetBegin, that.characterOffsetBegin) &&
                Objects.equals(characterOffsetEnd, that.characterOffsetEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, characterOffsetBegin, characterOffsetEnd);
    }

    @Override
    public String toString() {
        return "AnnotationBase{" +
                "id=" + id +
                ", characterOffsetBegin=" + characterOffsetBegin +
                ", characterOffsetEnd=" + characterOffsetEnd +
                "} " + super.toString();
    }
}
