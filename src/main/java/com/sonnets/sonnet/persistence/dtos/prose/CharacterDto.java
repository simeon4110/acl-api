package com.sonnets.sonnet.persistence.dtos.prose;

import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkema
 */
public class CharacterDto {
    private String id;
    @NotNull
    private String bookId;
    private String firstName;
    private String lastName;
    private String gender;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotNull String getBookId() {
        return bookId;
    }

    public void setBookId(@NotNull String bookId) {
        this.bookId = bookId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CharacterDto{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
