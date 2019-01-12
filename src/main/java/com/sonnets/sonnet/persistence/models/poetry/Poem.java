package com.sonnets.sonnet.persistence.models.poetry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.models.ModelConstants;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Version;
import com.sonnets.sonnet.persistence.models.base.*;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = ModelConstants.GET_ALL_POEMS,
                procedureName = ModelConstants.GET_ALL_POEMS_PROCEDURE
        ),
        @NamedStoredProcedureQuery(
                name = ModelConstants.GET_RANDOM_POEM,
                procedureName = ModelConstants.GET_RANDOM_POEM_PROCEDURE,
                parameters = {
                        @StoredProcedureParameter(name = ModelConstants.FORM_PARAM,
                                mode = ParameterMode.IN, type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = ModelConstants.GET_POEMS_BY_USER,
                procedureName = ModelConstants.GET_POEMS_BY_USER_PROCEDURE,
                parameters = {
                        @StoredProcedureParameter(name = ModelConstants.USER_NAME_PARAM,
                                mode = ParameterMode.IN, type = String.class)
                },
                resultClasses = {
                        Poem.class
                }
        )
})
@Indexed
@Entity
@DiscriminatorValue(TypeConstants.POEM)
public class Poem extends Item implements Serializable {
    private static final long serialVersionUID = 3631244231926795794L;
    @Field(name = SearchConstants.POEM_FORM, store = Store.YES, analyze = Analyze.NO, termVector = TermVector.NO)
    @Column
    private String form; // The form of genre of the poem.
    @Embedded
    private Confirmation confirmation;
    @Field(name = SearchConstants.TEXT, store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = SearchConstants.TEXT_ANALYZER)
    @ElementCollection(fetch = FetchType.EAGER)
    @IndexedEmbedded
    private List<String> text;
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "annotation_id")
    private Annotation annotation;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Version> versions;
    @JsonIgnore
    @Column
    private boolean processed;
    @Embedded
    private TopicModel topicModel;
    @Column
    private Integer pageNumber;
    @Column
    private boolean hidden;
    @Column
    private boolean testing;

    public Poem() {
        super();
        this.confirmation = new Confirmation();
        this.confirmation.setConfirmed(false);
        this.confirmation.setPendingRevision(false);
        this.processed = false;
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

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public TopicModel getTopicModel() {
        return topicModel;
    }

    public void setTopicModel(TopicModel topicModel) {
        this.topicModel = topicModel;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isTesting() {
        return testing;
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Poem poem = (Poem) o;
        return processed == poem.processed &&
                hidden == poem.hidden &&
                testing == poem.testing &&
                Objects.equals(form, poem.form) &&
                Objects.equals(confirmation, poem.confirmation) &&
                Objects.equals(text, poem.text) &&
                Objects.equals(annotation, poem.annotation) &&
                Objects.equals(versions, poem.versions) &&
                Objects.equals(topicModel, poem.topicModel) &&
                Objects.equals(pageNumber, poem.pageNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), form, confirmation, text, annotation, versions, processed, topicModel,
                pageNumber, hidden, testing);
    }

    @Override
    public String toString() {
        return "Poem{" +
                "form='" + form + '\'' +
                ", confirmation=" + confirmation +
                ", text=" + text +
                ", annotation=" + annotation +
                ", versions=" + versions +
                ", processed=" + processed +
                ", topicModel=" + topicModel +
                ", pageNumber=" + pageNumber +
                ", hidden=" + hidden +
                ", testing=" + testing +
                "} " + super.toString();
    }
}
