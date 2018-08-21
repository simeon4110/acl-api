package com.sonnets.sonnet.persistence.models.base;

import org.hibernate.search.annotations.DocumentId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Model for storing annotations. Annotations are not standalone, poems and sections have them.
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
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] annotationBlob;
    @Column
    private String description;

    public Annotation() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getAnnotationBlob() {
        return annotationBlob;
    }

    public void setAnnotationBlob(byte[] annotationBlob) {
        this.annotationBlob = annotationBlob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Annotation that = (Annotation) o;
        return Objects.equals(id, that.id) &&
                Arrays.equals(annotationBlob, that.annotationBlob) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), id, description);
        result = 31 * result + Arrays.hashCode(annotationBlob);
        return result;
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "id=" + id +
                ", annotationBlob=" + Arrays.toString(annotationBlob) +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
