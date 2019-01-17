package com.sonnets.sonnet.persistence.models.annotation;

import com.sonnets.sonnet.persistence.models.StoredProcedures;
import com.sonnets.sonnet.persistence.models.base.Auditable;
import org.hibernate.search.annotations.DocumentId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Model for storing annotations. Annotations are not standalone, poems and sections have them. This is used to store
 * raw json strings.
 *
 * @author Josh Harkema
 */
@Entity
public class Annotation extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -7131872492811694640L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Column(columnDefinition = StoredProcedures.BIG_STRING)
    private String annotationBody;

    public Annotation() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnnotationBody() {
        return annotationBody;
    }

    public void setAnnotationBody(String annotationBody) {
        this.annotationBody = annotationBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Annotation that = (Annotation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(annotationBody, that.annotationBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotationBody);
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "id=" + id +
                ", annotationBody='" + annotationBody + '\'' +
                '}';
    }
}
