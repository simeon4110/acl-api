package com.sonnets.sonnet.persistence.models.base;

import com.sonnets.sonnet.services.search.SearchConstants;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class is for storing different versions of the same text. It is not standalone and should only be embedded
 * in other objects (i.e. Poem or Section).
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
public class Version extends Item implements Serializable {
    private static final long serialVersionUID = 5008563910579946114L;
    @Field(name = SearchConstants.VERSION_TEXT, store = Store.YES, termVector = TermVector.YES)
    @Column(columnDefinition = "TEXT")
    private String text;

    public Version() {
        super();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Version version = (Version) o;
        return Objects.equals(text, version.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "Version{" +
                "text='" + text + '\'' +
                "} " + super.toString();
    }
}
