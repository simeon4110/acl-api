package com.sonnets.sonnet.persistence.models.poetry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemOutDto;
import com.sonnets.sonnet.persistence.models.base.*;
import com.sonnets.sonnet.persistence.models.base.Version;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * General purpose object for handling poetry type lit.
 *
 * @author Josh Harkema
 */
@SqlResultSetMapping(
        name = "PoemMap",
        classes = @ConstructorResult(
                targetClass = PoemOutDto.class,
                columns = {
                        @ColumnResult(name = "id", type = BigDecimal.class),
                        @ColumnResult(name = "title"),
                        @ColumnResult(name = "category"),
                        @ColumnResult(name = "description"),
                        @ColumnResult(name = "publication_year", type = Integer.class),
                        @ColumnResult(name = "publication_stmt"),
                        @ColumnResult(name = "source_desc"),
                        @ColumnResult(name = "period"),
                        @ColumnResult(name = "form"),
                        @ColumnResult(name = "confirmed", type = boolean.class),
                        @ColumnResult(name = "confirmed_at", type = Timestamp.class),
                        @ColumnResult(name = "confirmed_by"),
                        @ColumnResult(name = "pending_revision", type = boolean.class),
                        @ColumnResult(name = "author_id", type = BigDecimal.class),
                        @ColumnResult(name = "first_name"),
                        @ColumnResult(name = "last_name"),
                        @ColumnResult(name = "poem_text")
                }
        )
)
@Indexed
@Entity
@Table
@DiscriminatorValue("POEM")
public class Poem extends Item implements Serializable {
    private static final long serialVersionUID = 3631244231926795794L;
    @Field(name = "poem_form", store = Store.YES, analyze = Analyze.NO, termVector = TermVector.NO)
    @Column
    private String form; // The form of genre of the poem.
    @Embedded
    private Confirmation confirmation;
    @IndexedEmbedded
    @Field(name = "text", store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = "textAnalyzer")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> text;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annotation_id")
    @MapsId
    private Annotation annotation;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Version> versions;
    @JsonIgnore
    @Column
    private boolean processed;
    @Embedded
    private TopicModel topicModel;

    public Poem() {
        super();
        this.confirmation = new Confirmation();
        this.confirmation.setConfirmed(false);
        this.confirmation.setPendingRevision(false);
        this.processed = false;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Poem poem = (Poem) o;
        return processed == poem.processed &&
                Objects.equals(form, poem.form) &&
                Objects.equals(confirmation, poem.confirmation) &&
                Objects.equals(text, poem.text) &&
                Objects.equals(annotation, poem.annotation) &&
                Objects.equals(versions, poem.versions) &&
                Objects.equals(topicModel, poem.topicModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), form, confirmation, text, annotation, versions, processed, topicModel);
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
                "} " + super.toString();
    }
}
