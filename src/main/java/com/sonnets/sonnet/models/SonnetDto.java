package com.sonnets.sonnet.models;

import javax.validation.constraints.NotEmpty;

/**
 * DTO object handles input from controller. Only very basic input validation is done here. Bootstrap has front end,
 * implementation, but much more is needed.
 *
 * @author Josh Harkema
 */
public class SonnetDto {
    private Long id;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    private String title;
    @NotEmpty
    private String publicationYear;
    @NotEmpty
    private String publicationStmt;
    @NotEmpty
    private String sourceDesc;
    @NotEmpty
    private String text;

    public SonnetDto() {

    }

    public SonnetDto(Sonnet sonnet) {
        this.id = sonnet.getId();
        this.firstName = sonnet.getFirstName();
        this.lastName = sonnet.getLastName();
        this.title = sonnet.getTitle();
        this.publicationYear = sonnet.getPublicationYear();
        this.publicationStmt = sonnet.getPublicationStmt();
        this.sourceDesc = sonnet.getSourceDesc();
        this.text = sonnet.getTextPretty();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationStmt() {
        return publicationStmt;
    }

    public void setPublicationStmt(String publicationStmt) {
        this.publicationStmt = publicationStmt;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "SonnetDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

}
