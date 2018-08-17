package com.sonnets.sonnet.persistence.models.poetry;

import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.base.Version;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * General purpose object for handling poetry type lit.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table
@DiscriminatorValue("POEM")
public class Poem extends Item implements Serializable {
    private static final long serialVersionUID = 3631244231926795794L;
    @Field(name = "form", store = Store.YES)
    @Column
    private String form; // The form of genre of the poem.
    @Column
    private Confirmation confirmation;
    @Column
    @IndexedEmbedded
    @Field(name = "text", store = Store.YES, termVector = TermVector.YES)
    @ElementCollection
    private List<String> text;
    @OneToMany
    private List<Annotation> annotations;
    @OneToMany
    private List<Version> versions;

    public Poem() {
        super();
    }

    /**
     * This parses a Sonnet so it shows "pretty" in html <textarea></textarea> elements. (adds \n for newlines.)
     *
     * @return a nicely formatted string.
     */
    public String getTextPretty() {
        StringBuilder sb = new StringBuilder();
        for (String s : text) {
            s = s.trim();
            sb.append(s).append("\n");
        }

        return sb.toString();
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Poem poem = (Poem) o;
        return Objects.equals(form, poem.form) &&
                Objects.equals(confirmation, poem.confirmation) &&
                Objects.equals(text, poem.text) &&
                Objects.equals(annotations, poem.annotations) &&
                Objects.equals(versions, poem.versions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), form, confirmation, text, annotations, versions);
    }

    @Override
    public String toString() {
        return "Poem{" +
                "form='" + form + '\'' +
                ", confirmation=" + confirmation +
                ", text=" + text +
                ", annotations=" + annotations +
                ", versions=" + versions +
                "} " + super.toString();
    }
}
