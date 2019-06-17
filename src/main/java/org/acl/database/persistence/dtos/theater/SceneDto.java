package org.acl.database.persistence.dtos.theater;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class SceneDto {
    private Long id;
    @NotNull
    private Long playId;
    @NotNull
    private Long actId;
    @NotNull
    private int number;
    private String notes;
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(required = true)
    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    @ApiModelProperty(required = true)
    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    @ApiModelProperty(required = true)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "SceneDto{" +
                "id=" + id +
                ", playId=" + playId +
                ", actId=" + actId +
                ", number=" + number +
                ", notes='" + notes + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
