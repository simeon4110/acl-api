package com.sonnets.sonnet.persistence.models.prose;

import com.sonnets.sonnet.persistence.bridges.AuthorBridge;
import com.sonnets.sonnet.persistence.models.base.*;
import com.sonnets.sonnet.persistence.models.base.Version;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
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
public class Section extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -7556341244036061332L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
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
    @OneToMany
    private List<Annotation> annotations;
    @OneToMany
    private List<Version> versions;

    public Section() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        Section section = (Section) o;
        return Objects.equals(id, section.id) &&
                Objects.equals(title, section.title) &&
                Objects.equals(confirmation, section.confirmation) &&
                Objects.equals(text, section.text) &&
                Objects.equals(author, section.author) &&
                Objects.equals(annotations, section.annotations) &&
                Objects.equals(versions, section.versions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, confirmation, text, author, annotations, versions);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", confirmation=" + confirmation +
                ", text='" + text + '\'' +
                ", author=" + author +
                ", annotations=" + annotations +
                ", versions=" + versions +
                '}';
    }
}
