package com.sonnets.sonnet.persistence.dtos.base;

/**
 * @author Josh Harkema
 */
public class AnnotationDto {
    private String id;
    private String annotationBody;

    public AnnotationDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnnotationBody() {
        return annotationBody;
    }

    public void setAnnotationBody(String annotationBody) {
        this.annotationBody = annotationBody;
    }

    @Override
    public String toString() {
        return "AnnotationDto{" +
                "id='" + id + '\'' +
                ", annotationBody='" + annotationBody + '\'' +
                '}';
    }
}
