package org.acl.database.persistence.dtos.theater;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class ActDto {
    private Long id;
    @NotNull
    private Long playId;
    @NotNull
    private int number;
    private String notes;

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

    @Override
    public String toString() {
        return "ActDto{" +
                "id=" + id +
                ", playId=" + playId +
                ", number=" + number +
                ", notes='" + notes + '\'' +
                '}';
    }
}
