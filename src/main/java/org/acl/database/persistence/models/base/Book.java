package org.acl.database.persistence.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.acl.database.persistence.models.TypeConstants;

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
@Entity
@DiscriminatorValue(TypeConstants.BOOK)
public class Book extends Item implements Serializable {
    private static final long serialVersionUID = -5579725087589223758L;
    @Column
    private String type;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Section> sections;
    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
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
