package org.acl.database.persistence.dtos.theater;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DialogLinesDto {
    private Long id;
    @NotNull
    private Long actorId;
    @NotNull
    private Long playId;
    @NotNull
    private Long actId;
    @NotNull
    private Long sceneId;
    @NotEmpty
    private String body;
    @NotNull
    private int sequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(required = true)
    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
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
    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    @ApiModelProperty(required = true)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @ApiModelProperty(required = true)
    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "DialogLinesDto{" +
                "id=" + id +
                ", actorId=" + actorId +
                ", playId=" + playId +
                ", actId=" + actId +
                ", sceneId=" + sceneId +
                ", body='" + body + '\'' +
                ", sequence=" + sequence +
                '}';
    }
}
