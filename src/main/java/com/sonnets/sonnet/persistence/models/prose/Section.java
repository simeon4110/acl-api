package com.sonnets.sonnet.persistence.models.prose;

import com.sonnets.sonnet.persistence.bridges.AnnotationBridge;
import com.sonnets.sonnet.persistence.bridges.AuthorBridge;
import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Indexed
@Entity
@Table
@DiscriminatorValue("SECT")
public class Section extends Book {
    private static final long serialVersionUID = -7556341244036061332L;
    @Field(name = "section_title", store = Store.YES)
    @Column
    private String title;
    @Column
    private Confirmation confirmation;
    @Field(name = "section_text", store = Store.YES, termVector = TermVector.YES)
    @Column(columnDefinition = "LONGTEXT")
    private String text;
    @Field(name = "section_authors", store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = AuthorBridge.class)
    @ManyToOne
    private Author author;
    @Field(name = "section_annotations", store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = AnnotationBridge.class)
    @OneToMany
    private List<Annotation> annotations;

    public Section() {
        // Default constructor for spring data.
    }

    public String getTitle() {
        return title;
    }

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Section section = (Section) o;
        return Objects.equals(title, section.title) &&
                Objects.equals(confirmation, section.confirmation) &&
                Objects.equals(text, section.text) &&
                Objects.equals(author, section.author) &&
                Objects.equals(annotations, section.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, confirmation, text, author, annotations);
    }

    @Override
    public String toString() {
        return "Section{" +
                "title='" + title + '\'' +
                ", confirmation=" + confirmation +
                ", text='" + text + '\'' +
                ", author=" + author +
                ", annotations=" + annotations +
                "} " + super.toString();
    }
}
