package com.sonnets.sonnet.persistence.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.annotation.Annotation;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Standalone stories from Journals or whatever.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@DiscriminatorValue(TypeConstants.SHORT_STORY)
public class ShortStory extends Item implements Serializable {
    private static final long serialVersionUID = -2972048903052856414L;
    @Embedded
    private Confirmation confirmation;
    @Field(name = SearchConstants.TEXT, store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = SearchConstants.TEXT_ANALYZER)
    @Column(columnDefinition = "TEXT")
    private String text;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "annotation_id")
    private Annotation annotation;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Version> versions;

    public ShortStory() {
        super();
        this.confirmation = new Confirmation();
        this.confirmation.setConfirmed(false);
        this.confirmation.setPendingRevision(false);
        this.versions = new ArrayList<>();
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
        ShortStory that = (ShortStory) o;
        return Objects.equals(confirmation, that.confirmation) &&
                Objects.equals(text, that.text) &&
                Objects.equals(annotation, that.annotation) &&
                Objects.equals(versions, that.versions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), confirmation, text, annotation, versions);
    }

    @Override
    public String toString() {
        return "ShortStory{" +
                "confirmation=" + confirmation +
                ", text='" + text + '\'' +
                ", annotation=" + annotation +
                ", versions=" + versions +
                "} " + super.toString();
    }
}
