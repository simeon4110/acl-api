package com.sonnets.sonnet.persistence.models.base;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stores pre-loaded topic models.
 *
 * @author Josh Harkema
 */
@Indexed
@Embeddable
public class TopicModel implements Serializable {
    private static final long serialVersionUID = -8369936995631566616L;
    @Field(name = "topic_model", store = Store.YES, analyze = Analyze.NO)
    @ElementCollection(targetClass = HashMap.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "topic_model", joinColumns = @JoinColumn(name = "topic_model_id"))
    @Column(name = "model")
    private Map<Integer, Map<Double, String>> model;

    public Map<Integer, Map<Double, String>> getModel() {
        return model;
    }

    public void setModel(Map<Integer, Map<Double, String>> model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicModel that = (TopicModel) o;
        return Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model);
    }

    @Override
    public String toString() {
        return "TopicModel{" +
                "model=" + model +
                '}';
    }
}
