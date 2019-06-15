package org.acl.database.persistence.dtos.base;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkema
 */
public class PoemDto extends SourceDetailsDto {
    @NotNull
    private Long authorId;
    private String title;
    @NotEmpty
    private String period;
    @NotEmpty
    private String form;
    @NotEmpty
    private String text;

    public PoemDto() {
        super();
    }

    @ApiModelProperty(required = true)
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

    @ApiModelProperty(required = true)
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @ApiModelProperty(required = true)
    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @ApiModelProperty(required = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "PoemDto{" +
                ", authorId='" + authorId + '\'' +
                ", title='" + title + '\'' +
                ", period='" + period + '\'' +
                ", form='" + form + '\'' +
                ", text='" + text + '\'' +
                "} " + super.toString();
    }
}
