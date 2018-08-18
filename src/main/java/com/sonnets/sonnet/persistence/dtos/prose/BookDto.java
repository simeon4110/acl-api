package com.sonnets.sonnet.persistence.dtos.prose;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkema
 */
public class BookDto {
    private Long id;
    @NotNull
    private Long authorId;
    @NotEmpty
    private String title;
    private Integer publicationYear;
    @NotEmpty
    private String publicationStmt;
    @NotEmpty
    private String sourceDesc;
    @NotEmpty
    private String period;
    @NotEmpty
    private String category;
    @NotEmpty
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotEmpty Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(@NotEmpty Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "id=" + id +
                ", authorId='" + authorId + '\'' +
                ", title='" + title + '\'' +
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", period='" + period + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
