package org.acl.database.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;

public class OtherDto extends SourceDetailsDto {
    @NotEmpty
    private String authorId;
    private String category;
    private String title;
    private Integer publicationYear;
    @NotEmpty
    private String publicationStmt;
    @NotEmpty
    private String sourceDesc;
    @NotEmpty
    private String period;
    @NotEmpty
    private String text;

    public OtherDto() {
        super();
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "OtherDto{" +
                ", authorId='" + authorId + '\'' +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", period='" + period + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
