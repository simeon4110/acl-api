package com.sonnets.sonnet.persistence.models.web;


import com.sonnets.sonnet.persistence.models.base.Item;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

/**
 * Model allows users to add and store texts not available to other users.
 *
 * @author Josh Harkema
 */
@Entity
public class UserPrivateText extends Item implements Serializable {
    private static final long serialVersionUID = -5398920714584686611L;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String text;

    public UserPrivateText() {
        super();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserPrivateText that = (UserPrivateText) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "UserPrivateText{" +
                "text='" + text + '\'' +
                "} " + super.toString();
    }
}
