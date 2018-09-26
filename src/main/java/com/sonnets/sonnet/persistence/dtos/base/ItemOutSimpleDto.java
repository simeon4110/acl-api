package com.sonnets.sonnet.persistence.dtos.base;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A smaller version of the big ItemDto for speed and data minimization.
 *
 * @author Josh Harkema
 */
public class ItemOutSimpleDto implements Serializable {
    private static final long serialVersionUID = -7256193728535479636L;
    private BigDecimal id;
    private BigDecimal itemId;
    private String firstName;
    private String lastName;
    private String title;
    private String bookTitle;
    private String type;

    public ItemOutSimpleDto(BigDecimal id, BigDecimal itemId, String firstName, String lastName, String title,
                            String bookTitle, String type) {
        this.id = id;
        this.itemId = itemId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.bookTitle = bookTitle;
        this.type = type;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getItemId() {
        return itemId;
    }

    public void setItemId(BigDecimal itemId) {
        this.itemId = itemId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ItemOutSimpleDto{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
