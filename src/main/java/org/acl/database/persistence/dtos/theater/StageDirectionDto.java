package org.acl.database.persistence.dtos.theater;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class StageDirectionDto {
    private Long id;
    @NotEmpty
    private String body;
    @NotNull
    private Set<Long> actorIds;
    @NotNull
    private Long playId;
    @NotNull
    private Long sceneId;
    @NotNull
    private int sequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(required = true)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @ApiModelProperty(required = true)
    public Set<Long> getActorIds() {
        return actorIds;
    }

    public void setActorIds(Set<Long> actorIds) {
        this.actorIds = actorIds;
    }

    @ApiModelProperty(required = true)
    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    @ApiModelProperty(required = true)
    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
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
        return "StageDirectionDto{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", actorIds=" + actorIds +
                ", playId=" + playId +
                ", sceneId=" + sceneId +
                ", sequence=" + sequence +
                '}';
    }
}
