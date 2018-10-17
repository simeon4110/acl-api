package com.sonnets.sonnet.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkema
 */
public class AnnotationDto {
    private Long id;
    @NotEmpty
    private String type;
    @NotNull
    private Long itemId;
    private Long sectionId;
    private String itemFriendly;
    private String body;
    private Long characterOffsetBegin;
    private Long characterOffsetEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getItemFriendly() {
        return itemFriendly;
    }

    public void setItemFriendly(String itemFriendly) {
        this.itemFriendly = itemFriendly;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getCharacterOffsetBegin() {
        return characterOffsetBegin;
    }

    public void setCharacterOffsetBegin(Long characterOffsetBegin) {
        this.characterOffsetBegin = characterOffsetBegin;
    }

    public Long getCharacterOffsetEnd() {
        return characterOffsetEnd;
    }

    public void setCharacterOffsetEnd(Long characterOffsetEnd) {
        this.characterOffsetEnd = characterOffsetEnd;
    }

    @Override
    public String toString() {
        return "AnnotationDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", itemId=" + itemId +
                ", sectionId=" + sectionId +
                ", itemFriendly='" + itemFriendly + '\'' +
                ", body='" + body + '\'' +
                ", characterOffsetBegin=" + characterOffsetBegin +
                ", characterOffsetEnd=" + characterOffsetEnd +
                '}';
    }
}
