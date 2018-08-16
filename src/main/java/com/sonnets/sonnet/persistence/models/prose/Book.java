package com.sonnets.sonnet.persistence.models.prose;

import com.sonnets.sonnet.persistence.bridges.SectionBridge;
import com.sonnets.sonnet.persistence.models.base.Item;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Indexed
@Entity
@Table
@DiscriminatorValue("BOOK")
public class Book extends Item implements Serializable {
    private static final long serialVersionUID = -5579725087589223758L;
    @Column
    private Type type;
    @Field(name = "book_section", store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = SectionBridge.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Book() {
        super();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Book book = (Book) o;
        return type == book.type &&
                Objects.equals(sections, book.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, sections);
    }

    @Override
    public String toString() {
        return "Book{" +
                "type=" + type +
                ", sections=" + sections +
                "} " + super.toString();
    }

    enum Type {
        NONFICTION,
        FICTION,
        ESSAY,
        JOURNAL,
        NEWS,
        OTHER
    }
}
