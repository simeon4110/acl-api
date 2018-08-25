package com.sonnets.sonnet.persistence.models.base;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Indexed
@Embeddable
public class TopicModel implements Serializable {
    private static final long serialVersionUID = -8369936995631566616L;
    @Field(name = "topic_model", store = Store.YES, analyze = Analyze.NO)
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Integer, String> model;

    public Map<Integer, String> getModel() {
        return model;
    }

    public void setModel(Map<Integer, String> model) {
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
