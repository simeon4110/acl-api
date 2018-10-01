package com.sonnets.sonnet.persistence.models.base;

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
public class Dialog implements Serializable {
    private static final long serialVersionUID = -6924325189995488819L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Column
    @Field(name = "dialog_body", store = Store.YES, termVector = TermVector.YES)
    private String body;
    @Column
    private Long sectionId;

    public Dialog() {
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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dialog dialog = (Dialog) o;
        return Objects.equals(id, dialog.id) &&
                Objects.equals(body, dialog.body) &&
                Objects.equals(sectionId, dialog.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body, sectionId);
    }

    @Override
    public String toString() {
        return "Dialog{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", sectionId=" + sectionId +
                '}';
    }
}
