package org.acl.database.persistence.dtos.prose;

import io.swagger.annotations.ApiModelProperty;
import org.acl.database.persistence.dtos.base.SourceDetailsDto;

import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkema
 */
public class SectionDto extends SourceDetailsDto {
    private String title;
    private String text;
    private String description;
    @NotNull
    private Long bookId;
    @NotNull
    private Long authorId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ApiModelProperty(required = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(required = true)
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @ApiModelProperty(required = true)
    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "SectionDto{" +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", bookId=" + bookId +
                ", authorId=" + authorId +
                "} " + super.toString();
    }
}
