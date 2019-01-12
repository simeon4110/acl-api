package com.sonnets.sonnet.persistence.models.prose;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.sonnets.sonnet.persistence.bridges.CharacterListBridge;
import com.sonnets.sonnet.persistence.models.ModelConstants;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Version;
import com.sonnets.sonnet.persistence.models.base.*;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.*;

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
                name = ModelConstants.GET_ALL_SECTIONS,
                procedureName = ModelConstants.GET_ALL_SECTIONS_PROCEDURE
        ),
        @NamedStoredProcedureQuery(
                name = ModelConstants.GET_SECTIONS_BY_USER,
                procedureName = ModelConstants.GET_SECTIONS_BY_USER_PROCEDURE,
                parameters = {
                        @StoredProcedureParameter(name = ModelConstants.USER_NAME_PARAM,
                                mode = ParameterMode.IN, type = String.class)
                },
                resultClasses = {
                        Section.class
                }
        ),
        @NamedStoredProcedureQuery(
                name = ModelConstants.GET_BOOK_SECTIONS_SIMPLE,
                procedureName = ModelConstants.GET_BOOK_SECTIONS_SIMPLE_PROCEDURE,
                parameters = {
                        @StoredProcedureParameter(name = ModelConstants.BOOK_ID_PARAM,
                                mode = ParameterMode.IN, type = Long.class),
                        @StoredProcedureParameter(name = ModelConstants.OUTPUT_PARAM,
                                mode = ParameterMode.OUT, type = String.class)
                }
        )
})
@Indexed
@Entity
@DiscriminatorValue(TypeConstants.SECTION)
@AnalyzerDef(name = SearchConstants.TEXT_ANALYZER,
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                        @Parameter(name = "language", value = "English")
                })
        })
public class Section extends Item implements Serializable {
    private static final long serialVersionUID = -7556341244036061332L;
    @Embedded
    private Confirmation confirmation;
    @Field(name = SearchConstants.TEXT, store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = SearchConstants.TEXT_ANALYZER)
    @Column(columnDefinition = ModelConstants.BIG_STRING)
    @Expose
    private String text;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "annotation_id")
    private Annotation annotation;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Version> versions;
    @JsonIgnore
    @Column
    private boolean processed;
    @Column
    @Expose
    private Long parentId;
    @Embedded
    private TopicModel topicModel;
    @Field(name = SearchConstants.NARRATOR, store = Store.YES, analyze = Analyze.NO, termVector = TermVector.NO)
    @FieldBridge(impl = CharacterListBridge.class)
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
                Objects.equals(topicModel, section.topicModel) &&
                Objects.equals(narrator, section.narrator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), confirmation, text, annotation, versions, processed, parentId, topicModel,
                narrator);
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
                ", topicModel=" + topicModel +
                ", narrator=" + narrator +
                "} " + super.toString();
    }
}
