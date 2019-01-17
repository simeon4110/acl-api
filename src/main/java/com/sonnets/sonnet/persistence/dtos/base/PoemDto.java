package com.sonnets.sonnet.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkema
 */
public class PoemDto extends SourceDetailsDto {
    private Long id;
    @NotNull
    private Long authorId;
    private String title;
    @NotEmpty
    private String period;
    private String form;
    @NotEmpty
    private String text;

    public PoemDto() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "PoemDto{" +
                "id=" + id +
                ", authorId='" + authorId + '\'' +
                ", title='" + title + '\'' +
                ", period='" + period + '\'' +
                ", form='" + form + '\'' +
                ", text='" + text + '\'' +
                "} " + super.toString();
    }
}
