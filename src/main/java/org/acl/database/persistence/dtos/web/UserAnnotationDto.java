package org.acl.database.persistence.dtos.web;

/**
 * Simple POJO for moving annotations from JSON to object.
 *
 * @author Josh Harkema
 */
public class UserAnnotationDto {
    private String id;
    private String parentId;
    private String parentType;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "UserAnnotationDto{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", parentType='" + parentType + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
