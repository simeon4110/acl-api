package com.sonnets.sonnet.persistence.models.annotation_types;

import com.sonnets.sonnet.persistence.models.base.Auditable;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Stores all the dialog of a given character.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table
public class Dialog extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -6924325189995488819L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    @Field(name = "dialog_body", store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = "textAnalyzer")
    private String body;
    @Column
    private Long itemId;
    @Column
    private String itemFriendly;
    @Column
    private Long sectionId;
    @Column
    private Long characterOffsetBegin;
    @Column
    private Long characterOffsetEnd;

    public Dialog() {
        // Empty by design.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public Long getCharacterOffsetBegin() {
        return characterOffsetBegin;
    }

    public void setCharacterOffsetBegin(Long characterOffsetBegin) {
        this.characterOffsetBegin = characterOffsetBegin;
    }

    public Long getCharacterOffsetEnd() {
        return characterOffsetEnd;
    }

    public void setCharacterOffsetEnd(Long characterOffsetEnd) {
        this.characterOffsetEnd = characterOffsetEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Dialog dialog = (Dialog) o;
        return Objects.equals(id, dialog.id) &&
                Objects.equals(body, dialog.body) &&
                Objects.equals(itemId, dialog.itemId) &&
                Objects.equals(itemFriendly, dialog.itemFriendly) &&
                Objects.equals(sectionId, dialog.sectionId) &&
                Objects.equals(characterOffsetBegin, dialog.characterOffsetBegin) &&
                Objects.equals(characterOffsetEnd, dialog.characterOffsetEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, body, itemId, itemFriendly, sectionId,
                characterOffsetBegin, characterOffsetEnd);
    }

    @Override
    public String toString() {
        return "Dialog{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", itemId=" + itemId +
                ", itemFriendly='" + itemFriendly + '\'' +
                ", sectionId=" + sectionId +
                ", characterOffsetBegin=" + characterOffsetBegin +
                ", characterOffsetEnd=" + characterOffsetEnd +
                "} " + super.toString();
    }
}
