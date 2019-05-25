package com.sonnets.sonnet.persistence.models.web;

import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Model object for corpora.
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "corpora")
public class Corpora extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -3561569564692824043L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @ManyToAny(metaColumn = @Column(name = "item_type", length = 4), fetch = FetchType.LAZY)
    @AnyMetaDef(
            metaType = "string", idType = "long",
            metaValues = {
                    @MetaValue(targetEntity = Book.class, value = TypeConstants.BOOK),
                    @MetaValue(targetEntity = Poem.class, value = TypeConstants.POEM),
                    @MetaValue(targetEntity = Section.class, value = TypeConstants.SECTION),
                    @MetaValue(targetEntity = Other.class, value = TypeConstants.OTHER)
            }
    )
    @Cascade({CascadeType.ALL})
    @JoinTable(
            name = "corpora_items",
            joinColumns = @JoinColumn(name = "item_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "corpora_id", nullable = false)
    )
    private Set<Item> items = new HashSet<>();

    private int totalItems;

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

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
        this.totalItems = this.items.size();
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Corpora corpora = (Corpora) o;
        return totalItems == corpora.totalItems &&
                Objects.equals(id, corpora.id) &&
                Objects.equals(name, corpora.name) &&
                Objects.equals(description, corpora.description) &&
                Objects.equals(items, corpora.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, description, items, totalItems);
    }

    @Override
    public String toString() {
        return "Corpora{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", items=" + items +
                ", totalItems=" + totalItems +
                "} " + super.toString();
    }
}
