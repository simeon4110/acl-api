package com.sonnets.sonnet.persistence.models.web;

import com.sonnets.sonnet.persistence.models.annotation_types.Dialog;
import com.sonnets.sonnet.persistence.models.base.Auditable;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.models.prose.Section;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.DocumentId;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Model object for corpera.
 *
 * @author Josh Harkema
 */
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "addCorporaItem",
                procedureName = "add_corpora_item",
                parameters = {
                        @StoredProcedureParameter(name = "corporaId", mode = ParameterMode.IN, type = Long.class),
                        @StoredProcedureParameter(name = "itemId", mode = ParameterMode.IN, type = Long.class),
                        @StoredProcedureParameter(name = "itemType", mode = ParameterMode.IN, type = String.class)
                }

        ),
        @NamedStoredProcedureQuery(
                name = "getCorpora",
                procedureName = "get_corpora",
                parameters = {
                        @StoredProcedureParameter(name = "corporaId", mode = ParameterMode.IN, type = Long.class)
                },
                resultSetMappings = {
                        "CorporaMap"
                }
        ),
        @NamedStoredProcedureQuery(
                name = "getCorporaItems",
                procedureName = "get_corpora_items",
                parameters = {
                        @StoredProcedureParameter(name = "corporaId", mode = ParameterMode.IN, type = Long.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "getCorporaItemsSimple",
                procedureName = "get_corpora_items_simple",
                parameters = {
                        @StoredProcedureParameter(name = "corporaId", mode = ParameterMode.IN, type = Long.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "getCorporaUser",
                procedureName = "get_corpora_user",
                parameters = {
                        @StoredProcedureParameter(name = "createdBy", mode = ParameterMode.IN, type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "deleteCorporaItem",
                procedureName = "delete_corpora_item",
                parameters = {
                        @StoredProcedureParameter(name = "itemType", mode = ParameterMode.IN, type = String.class),
                        @StoredProcedureParameter(name = "itemId", mode = ParameterMode.IN, type = Long.class),
                        @StoredProcedureParameter(name = "corporaId", mode = ParameterMode.IN, type = Long.class)
                }
        )
})
@SqlResultSetMapping(
        name = "CorporaMap",
        classes = @ConstructorResult(
                targetClass = Corpora.class,
                columns = {
                        @ColumnResult(name = "id", type = BigDecimal.class),
                        @ColumnResult(name = "created_by"),
                        @ColumnResult(name = "created_date", type = Date.class),
                        @ColumnResult(name = "last_modified_by"),
                        @ColumnResult(name = "last_modified_date", type = Date.class),
                        @ColumnResult(name = "description"),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "total_items", type = int.class)
                }
        )
)
@Entity
@Table(name = "corpora")
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
    @ManyToAny(metaDef = "itemMetaDef", metaColumn = @Column(name = "item_type", length = 4), fetch = FetchType.LAZY)
    @AnyMetaDef(
            name = "itemMetaDef", metaType = "string", idType = "long",
            metaValues = {
                    @MetaValue(targetEntity = Book.class, value = "BOOK"),
                    @MetaValue(targetEntity = Poem.class, value = "POEM"),
                    @MetaValue(targetEntity = Section.class, value = "SECT"),
                    @MetaValue(targetEntity = Other.class, value = "OTHR"),
                    @MetaValue(targetEntity = BookCharacter.class, value = "BKCR"),
                    @MetaValue(targetEntity = Dialog.class, value = "DIAL")
            }
    )
    @JoinTable(
            name = "corpora_items",
            joinColumns = @JoinColumn(name = "corpora_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "item_id", nullable = false)
    )
    @Cascade({CascadeType.DETACH, CascadeType.REMOVE})
    private Set<Item> items = new HashSet<>();

    private int totalItems;

    public Corpora() {
        super();
    }

    public Corpora(final BigDecimal id, final String createdBy, final Date createdDate, final String lastModifiedBy,
                   final Date lastModifiedDate, final String description, final String name, final int totalItems) {
        super();
        super.setCreatedBy(createdBy);
        super.setCreatedDate(createdDate);
        super.setLastModifiedBy(lastModifiedBy);
        super.setLastModifiedDate(lastModifiedDate);
        this.id = id.longValue();
        this.description = description;
        this.name = name;
        this.totalItems = totalItems;
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
