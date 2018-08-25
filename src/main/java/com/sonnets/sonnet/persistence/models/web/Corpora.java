package com.sonnets.sonnet.persistence.models.web;

import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.Auditable;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.base.Version;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.models.prose.Section;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.DocumentId;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
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
public class Corpora extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -3561569564692824043L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @ManyToAny(
            metaColumn = @Column(name = "item_type", length = 4),
            fetch = FetchType.EAGER
    )
    @AnyMetaDef(
            metaType = "string", idType = "long",
            metaValues = {
                    @MetaValue(targetEntity = Book.class, value = "BOOK"),
                    @MetaValue(targetEntity = Poem.class, value = "POEM"),
                    @MetaValue(targetEntity = Section.class, value = "SECT"),
                    @MetaValue(targetEntity = Other.class, value = "OTHR"),
                    @MetaValue(targetEntity = Version.class, value = "VERS"),
                    @MetaValue(targetEntity = Annotation.class, value = "ANNO")
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
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return Objects.equals(id, corpora.id) &&
                Objects.equals(name, corpora.name) &&
                Objects.equals(description, corpora.description) &&
                Objects.equals(items, corpora.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, description, items);
    }

    @Override
    public String toString() {
        return "Corpora{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", items=" + items +
                "} " + super.toString();
    }
}
