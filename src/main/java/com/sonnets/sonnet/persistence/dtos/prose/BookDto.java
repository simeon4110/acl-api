package com.sonnets.sonnet.persistence.dtos.prose;

import com.sonnets.sonnet.persistence.dtos.base.SourceDetailsDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkema
 */
public class BookDto extends SourceDetailsDto {
    @NotNull
    private Long authorId;
    @NotEmpty
    private String title;
    @NotEmpty
    private String period;
    @NotEmpty
    private String type;

    public BookDto() {
        super();
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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
                "authorId=" + authorId +
                ", title='" + title + '\'' +
                ", period='" + period + '\'' +
                ", type='" + type + '\'' +
                "} " + super.toString();
    }
}
