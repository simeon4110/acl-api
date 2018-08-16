package com.sonnets.sonnet.persistence.models.web;

import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Section;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

/**
 * Model object for corpera.
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "corpora")
@DiscriminatorValue("CORP")
public class Corpora extends Item {
    private static final long serialVersionUID = -3561569564692824043L;
    @Column
    private String name;
    @ManyToAny(
            metaColumn = @Column(name = "item_type", length = 4)
    )
    @AnyMetaDef(
            metaType = "string", idType = "long",
            metaValues = {
                    @MetaValue(targetEntity = Book.class, value = "BOOK"),
                    @MetaValue(targetEntity = Poem.class, value = "POEM"),
                    @MetaValue(targetEntity = Section.class, value = "SECT")
            }
    )
    @Cascade({CascadeType.ALL})
    @JoinTable(
            name = "corpora_items",
            joinColumns = @JoinColumn(name = "corpora_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;

    public Corpora() {
        // Default constructor for spring data.
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Corpora corpora = (Corpora) o;
        return Objects.equals(name, corpora.name) &&
                Objects.equals(items, corpora.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, items);
    }

    @Override
    public String toString() {
        return "Corpora{" +
                "name='" + name + '\'' +
                ", items=" + items +
                "} " + super.toString();
    }
}
