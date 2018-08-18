package com.sonnets.sonnet.persistence.models.prose;

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
@Indexed
@Entity
@Table
@DiscriminatorValue("BOOK")
public class Book extends Item implements Serializable {
    private static final long serialVersionUID = -5579725087589223758L;
    @Column
    private String type;
    @Field(name = "book_section", store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = SectionBridge.class)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Section> sections;
    @Field(name = "book_character", store = Store.YES, termVector = TermVector.YES)
    @FieldBridge(impl = CharacterBridge.class)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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
