package com.sonnets.sonnet.persistence.dtos.base;

import com.sonnets.sonnet.persistence.models.base.Author;

/**
 * Massive all-purpose search related DTO.
 *
 * @author Josh Harkema
 */
public class SearchDto {
    // General fields.
    private Long id;
    private String category;
    private Author author;
    private String title;
    private int publicationYear;
    private String period;
    private String topics;
    private String text;

    // Poem specific.
    private String form;

    // Book specific.
    private String type;

    // Other specific.
    private String subType;

    // Book character specific.
    private String charFirstName;
    private String charLastName;
    private String charGender;

    private boolean searchPoems;
    private boolean searchBooks;
    private boolean searchBookCharacters;
    private boolean searchDialog;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getCharFirstName() {
        return charFirstName;
    }

    public void setCharFirstName(String charFirstName) {
        this.charFirstName = charFirstName;
    }

    public String getCharLastName() {
        return charLastName;
    }

    public void setCharLastName(String charLastName) {
        this.charLastName = charLastName;
    }

    public String getCharGender() {
        return charGender;
    }

    public void setCharGender(String charGender) {
        this.charGender = charGender;
    }

    public boolean isSearchPoems() {
        return searchPoems;
    }

    public void setSearchPoems(boolean searchPoems) {
        this.searchPoems = searchPoems;
    }

    public boolean isSearchBooks() {
        return searchBooks;
    }

    public void setSearchBooks(boolean searchBooks) {
        this.searchBooks = searchBooks;
    }

    public boolean isSearchBookCharacters() {
        return searchBookCharacters;
    }

    public void setSearchBookCharacters(boolean searchBookCharacters) {
        this.searchBookCharacters = searchBookCharacters;
    }

    public boolean isSearchDialog() {
        return searchDialog;
    }

    public void setSearchDialog(boolean searchDialog) {
        this.searchDialog = searchDialog;
    }

    @Override
    public String toString() {
        return "SearchDto{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", publicationYear=" + publicationYear +
                ", period='" + period + '\'' +
                ", topics='" + topics + '\'' +
                ", text='" + text + '\'' +
                ", form='" + form + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", charFirstName='" + charFirstName + '\'' +
                ", charLastName='" + charLastName + '\'' +
                ", charGender='" + charGender + '\'' +
                ", searchPoems=" + searchPoems +
                ", searchBooks=" + searchBooks +
                ", searchBookCharacters=" + searchBookCharacters +
                ", searchDialog=" + searchDialog +
                '}';
    }
}
