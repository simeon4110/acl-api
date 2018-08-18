package com.sonnets.sonnet.persistence.models.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

/**
 * Model for storing annotations. Annotations are not standalone, poems and sections use them.
 *
 * @author Josh Harkema
 */
@Entity
public class Annotation extends Item implements Serializable {
    private static final long serialVersionUID = -7131872492811694640L;
    @Column(columnDefinition = "LONGTEXT")
    private String text;
    @Column
    private Confirmation confirmation;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Annotation() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Annotation that = (Annotation) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "text='" + text + '\'' +
                "} " + super.toString();
    }
}