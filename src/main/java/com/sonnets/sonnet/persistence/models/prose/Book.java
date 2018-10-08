package com.sonnets.sonnet.persistence.models.prose;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.bridges.CharacterBridge;
import com.sonnets.sonnet.persistence.bridges.SectionBridge;
import com.sonnets.sonnet.persistence.models.base.Item;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * General purpose object for storing Books. A book is a collection of sections. The book itself stores nothing more
 * than a reference to sections.
 *
 * @author Josh Harkema
 */
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "getBookTitle",
                procedureName = "get_book_title",
                parameters = {
                        @StoredProcedureParameter(name = "bookId", mode = ParameterMode.IN, type = Long.class),
                        @StoredProcedureParameter(name = "title", mode = ParameterMode.INOUT, type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "getBooksSimple",
                procedureName = "get_books_simple",
                parameters = {
                        @StoredProcedureParameter(name = "output", mode = ParameterMode.OUT, type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "getBookCharacters",
                procedureName = "get_book_characters",
                parameters = {
                        @StoredProcedureParameter(name = "bookId", mode = ParameterMode.IN, type = Long.class)
                }
        )
})
@Indexed
@Entity
@DiscriminatorValue("BOOK")
public class Book extends Item implements Serializable {
    private static final long serialVersionUID = -5579725087589223758L;
    @Field(name = "book_type", store = Store.YES, termVector = TermVector.NO)
    @Column
    private String type;
    @JsonIgnore
    @Field(name = "book_section", store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = SectionBridge.class)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Section> sections;
    @JsonIgnore
    @Field(name = "book_character", store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = CharacterBridge.class)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "book_characters", joinColumns = {
            @JoinColumn(name = "book_id", referencedColumnName = "id"),
            @JoinColumn(name = "character_id", referencedColumnName = "id")
    })
    private List<BookCharacter> bookCharacters;

    public Book() {
        super();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<BookCharacter> getBookCharacters() {
        return bookCharacters;
    }

    public void setBookCharacters(List<BookCharacter> bookCharacters) {
        this.bookCharacters = bookCharacters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Book book = (Book) o;
        return Objects.equals(type, book.type) &&
                Objects.equals(sections, book.sections) &&
                Objects.equals(bookCharacters, book.bookCharacters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, sections, bookCharacters);
    }

    @Override
    public String toString() {
        return "Book{" +
                "type='" + type + '\'' +
                ", sections=" + sections +
                ", bookCharacters=" + bookCharacters +
                "} " + super.toString();
    }
}
