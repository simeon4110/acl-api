package com.sonnets.sonnet.persistence.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.bridges.CharacterListBridge;
import com.sonnets.sonnet.persistence.bridges.SectionBridge;
import com.sonnets.sonnet.persistence.models.StoredProcedureConstants;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.services.search.SearchConstants;
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
                name = StoredProcedureConstants.GET_ALL_BOOKS_SIMPLE,
                procedureName = StoredProcedureConstants.GET_ALL_BOOKS_SIMPLE_PROCEDURE
        ),
        @NamedStoredProcedureQuery(
                name = StoredProcedureConstants.GET_ALL_BOOKS_SIMPLE_PDO,
                procedureName = StoredProcedureConstants.GET_ALL_BOOKS_SIMPLE_PDO_PROCEDURE
        )
})
@Indexed
@Entity
@DiscriminatorValue(TypeConstants.BOOK)
public class Book extends Item implements Serializable {
    private static final long serialVersionUID = -5579725087589223758L;
    @Field(name = SearchConstants.BOOK_TYPE, store = Store.YES, termVector = TermVector.NO)
    @Column
    private String type;
    @Field(name = SearchConstants.BOOK_SECTION, store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = SectionBridge.class)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Section> sections;
    @Field(name = SearchConstants.BOOK_CHARACTER, store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = CharacterListBridge.class)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "book_characters", joinColumns = {
            @JoinColumn(name = "book_id", referencedColumnName = "id"),
            @JoinColumn(name = "character_id", referencedColumnName = "id")
    })
    @JsonIgnore
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
