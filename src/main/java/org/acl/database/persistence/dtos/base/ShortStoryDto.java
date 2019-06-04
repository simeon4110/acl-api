package org.acl.database.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ShortStoryDto extends SourceDetailsDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;
    @NotNull
    private Long authorId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "ShortStoryDto{" +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", authorId=" + authorId +
                "} " + super.toString();
    }
}
