package org.acl.database.persistence.dtos.theater;

import io.swagger.annotations.ApiModelProperty;
import org.acl.database.persistence.dtos.base.SourceDetailsDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PlayDto extends SourceDetailsDto {
    @NotNull
    private Long authorId;
    @NotEmpty
    private String title;
    @NotEmpty
    private String period;

    public PlayDto() {
        super();
    }

    @ApiModelProperty(required = true)
    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @ApiModelProperty(required = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ApiModelProperty(required = true)
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "PlayDto{" +
                "authorId=" + authorId +
                ", title='" + title + '\'' +
                ", period='" + period + '\'' +
                '}';
    }
}
