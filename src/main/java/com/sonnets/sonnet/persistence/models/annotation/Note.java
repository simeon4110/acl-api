package com.sonnets.sonnet.persistence.models.annotation;

import com.sonnets.sonnet.persistence.models.TypeConstants;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Basic note object for storing searchable user notes.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table
@DiscriminatorValue(TypeConstants.NOTE)
public class Note extends AnnotationBase {
    private static final long serialVersionUID = 8127083181575765535L;
    @Column
    private String itemType;
    @Column
    private Long itemId;
    @Column(columnDefinition = "TEXT")
    private String body;

    public Note() {
        super();
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Note note = (Note) o;
        return Objects.equals(itemType, note.itemType) &&
                Objects.equals(itemId, note.itemId) &&
                Objects.equals(body, note.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemType, itemId, body);
    }

    @Override
    public String toString() {
        return "Note{" +
                "itemType='" + itemType + '\'' +
                ", itemId=" + itemId +
                ", body='" + body + '\'' +
                "} " + super.toString();
    }
}
