package org.acl.database.persistence.models.annotation;

import org.acl.database.persistence.models.TypeConstants;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Stores all the dialog of a given character.
 *
 * @author Josh Harkema
 */
@Entity
@Table
@DiscriminatorValue(TypeConstants.DIALOG)
public class Dialog extends AnnotationBase {
    private static final long serialVersionUID = -8827348792053261353L;
    @Column
    private Long itemId;
    @Column
    private String itemFriendly;
    @Column
    private Long sectionId;
    @Column(columnDefinition = "TEXT")
    private String body;

    public Dialog() {
        super();
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemFriendly() {
        return itemFriendly;
    }

    public void setItemFriendly(String itemFriendly) {
        this.itemFriendly = itemFriendly;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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
        Dialog dialog = (Dialog) o;
        return Objects.equals(itemId, dialog.itemId) &&
                Objects.equals(itemFriendly, dialog.itemFriendly) &&
                Objects.equals(sectionId, dialog.sectionId) &&
                Objects.equals(body, dialog.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemId, itemFriendly, sectionId, body);
    }

    @Override
    public String toString() {
        return "DialogLines{" +
                "itemId=" + itemId +
                ", itemFriendly='" + itemFriendly + '\'' +
                ", sectionId=" + sectionId +
                ", body='" + body + '\'' +
                "} " + super.toString();
    }
}
