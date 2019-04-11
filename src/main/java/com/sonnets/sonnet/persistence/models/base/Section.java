package com.sonnets.sonnet.persistence.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.models.StoredProcedureConstants;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.annotation.Annotation;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sections are general purpose text objects stored in books. A section is *technically* standalone.
 *
 * @author Josh Harkema
 */
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = StoredProcedureConstants.GET_BOOK_SECTIONS_SIMPLE,
                procedureName = StoredProcedureConstants.GET_BOOK_SECTIONS_SIMPLE_PROCEDURE,
                parameters = {
                        @StoredProcedureParameter(name = StoredProcedureConstants.BOOK_ID_PARAM,
                                mode = ParameterMode.IN, type = Long.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = StoredProcedureConstants.GET_ALL_SECTIONS_SIMPLE,
                procedureName = StoredProcedureConstants.GET_ALL_SECTIONS_SIMPLE_PROCEDURE
        ),
        @NamedStoredProcedureQuery(
                name = StoredProcedureConstants.GET_ALL_SECTIONS_SIMPLE_PDO,
                procedureName = StoredProcedureConstants.GET_ALL_SECTIONS_SIMPLE_PDO_PROCEDURE
        ),
})
@Entity
@DiscriminatorValue(TypeConstants.SECTION)
public class Section extends Item implements Serializable {
    private static final long serialVersionUID = -7556341244036061332L;
    @Embedded
    private Confirmation confirmation;
    @Column(columnDefinition = "TEXT")
    private String text;
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "annotation_id")
    private Annotation annotation;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Version> versions;
    @JsonIgnore
    @Column
    private boolean processed;
    @Column
    private Long parentId;
    @Column
    private String parentTitle;
    @JsonIgnore
    @Embedded
    private TopicModel topicModel;
    @ManyToOne(fetch = FetchType.EAGER)
    private BookCharacter narrator;

    public Section() {
        super();
        this.confirmation = new Confirmation();
        this.confirmation.setConfirmed(false);
        this.confirmation.setPendingRevision(false);
        this.versions = new ArrayList<>();
        this.processed = false;
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

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public TopicModel getTopicModel() {
        return topicModel;
    }

    public void setTopicModel(TopicModel topicModel) {
        this.topicModel = topicModel;
    }

    public BookCharacter getNarrator() {
        return narrator;
    }

    public void setNarrator(BookCharacter narrator) {
        this.narrator = narrator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Section section = (Section) o;
        return processed == section.processed &&
                Objects.equals(confirmation, section.confirmation) &&
                Objects.equals(text, section.text) &&
                Objects.equals(annotation, section.annotation) &&
                Objects.equals(versions, section.versions) &&
                Objects.equals(parentId, section.parentId) &&
                Objects.equals(parentTitle, section.parentTitle) &&
                Objects.equals(topicModel, section.topicModel) &&
                Objects.equals(narrator, section.narrator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), confirmation, text, annotation, versions, processed, parentId,
                parentTitle, topicModel, narrator);
    }

    @Override
    public String toString() {
        return "Section{" +
                "confirmation=" + confirmation +
                ", text='" + text + '\'' +
                ", annotation=" + annotation +
                ", versions=" + versions +
                ", processed=" + processed +
                ", parentId=" + parentId +
                ", parentTitle='" + parentTitle + '\'' +
                ", topicModel=" + topicModel +
                ", narrator=" + narrator +
                "} " + super.toString();
    }
}
