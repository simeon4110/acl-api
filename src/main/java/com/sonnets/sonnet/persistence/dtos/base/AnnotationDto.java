package com.sonnets.sonnet.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;

/**
 * @author Josh Harkema
 */
public class AnnotationDto {
    @NotEmpty
    private String parentId;
    private String annotationId;
    private String description;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AnnotationDto{" +
                "parentId='" + parentId + '\'' +
                ", annotationId='" + annotationId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
