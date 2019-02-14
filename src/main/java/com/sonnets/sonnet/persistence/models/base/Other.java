package com.sonnets.sonnet.persistence.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.annotation.Annotation;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A general use model for everything that isn't a book/poem/book section.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table
@DiscriminatorValue(TypeConstants.OTHER)
public class Other extends Item implements Serializable {
    private static final long serialVersionUID = -1512828565413718191L;
    @Field(name = SearchConstants.OTHER_SUB_TYPE, store = Store.YES)
    @Column
    private String subType;
    @Embedded
    private Confirmation confirmation;
    @Field(name = SearchConstants.OTHER_TEXT, store = Store.YES, termVector = TermVector.YES)
    @Column(columnDefinition = "TEXT")
    private String text;
    @Column
    private Annotation annotation;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Version> versions;

    public Other() {
        super();
        this.confirmation = new Confirmation();
        confirmation.setConfirmed(false);
        confirmation.setPendingRevision(false);
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
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
        Other other = (Other) o;
        return Objects.equals(subType, other.subType) &&
                Objects.equals(confirmation, other.confirmation) &&
                Objects.equals(text, other.text) &&
                Objects.equals(annotation, other.annotation) &&
                Objects.equals(versions, other.versions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subType, confirmation, text, annotation, versions);
    }

    @Override
    public String toString() {
        return "Other{" +
                "subType='" + subType + '\'' +
                ", confirmation=" + confirmation +
                ", text='" + text + '\'' +
                ", annotation=" + annotation +
                ", versions=" + versions +
                "} " + super.toString();
    }
}
