package com.sonnets.sonnet.persistence.models.prose;

import com.sonnets.sonnet.persistence.models.base.*;
import com.sonnets.sonnet.persistence.models.base.Version;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

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
@Indexed
@Entity
@DiscriminatorValue("SECT")
@AnalyzerDef(name = "textAnalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                        @Parameter(name = "language", value = "English")
                })
        })
public class Section extends Item implements Serializable {
    private static final long serialVersionUID = -7556341244036061332L;
    @Field(name = "section_title", store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = "textAnalyzer")
    @Column
    private String title;
    @Column
    private Confirmation confirmation;
    @Field(name = "text", store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = "textAnalyzer")
    @Column(columnDefinition = "MEDIUMTEXT")
    private String text;
    @Column
    private Annotation annotation;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Version> versions;
    @Column
    private boolean processed;
    @Column
    private Long parentId;
    @Column
    private TopicModel topicModel;

    public Section() {
        super();
        this.confirmation = new Confirmation();
        this.confirmation.setConfirmed(false);
        this.confirmation.setPendingRevision(false);
        this.versions = new ArrayList<>();
        this.processed = false;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Section section = (Section) o;
        return processed == section.processed &&
                Objects.equals(title, section.title) &&
                Objects.equals(confirmation, section.confirmation) &&
                Objects.equals(text, section.text) &&
                Objects.equals(annotation, section.annotation) &&
                Objects.equals(versions, section.versions) &&
                Objects.equals(parentId, section.parentId) &&
                Objects.equals(topicModel, section.topicModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, confirmation, text, annotation, versions, processed, parentId,
                topicModel);
    }

    @Override
    public String toString() {
        return "Section{" +
                "title='" + title + '\'' +
                ", confirmation=" + confirmation +
                ", text='" + text + '\'' +
                ", annotation=" + annotation +
                ", versions=" + versions +
                ", processed=" + processed +
                ", parentId=" + parentId +
                ", topicModel=" + topicModel +
                "} " + super.toString();
    }
}
