package com.sonnets.sonnet.persistence.models.base;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table
public class UserAnnotation extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = 9005896807547547835L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private long parentId;
    @Column
    private String parentType;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String text;
    @Embedded
    private Confirmation confirmation;

    public UserAnnotation() {
        // Empty by design.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }


    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserAnnotation that = (UserAnnotation) o;
        return parentId == that.parentId &&
                Objects.equals(id, that.id) &&
                Objects.equals(parentType, that.parentType) &&
                Objects.equals(text, that.text) &&
                Objects.equals(confirmation, that.confirmation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, parentId, parentType, text, confirmation);
    }

    @Override
    public String toString() {
        return "UserAnnotation{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", parentType='" + parentType + '\'' +
                ", text='" + text + '\'' +
                ", confirmation=" + confirmation +
                "} " + super.toString();
    }
}
