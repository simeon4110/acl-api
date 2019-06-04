package org.acl.database.persistence.models.tools;

import org.acl.database.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author Josh Harkema
 */
@Entity
@Table
public class CustomStopWords extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -7591723029140211924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "custom_stop_words_words", joinColumns = @JoinColumn(name = "custom_stop_words_id"))
    @Column
    private List<String> words;

    public CustomStopWords() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomStopWords that = (CustomStopWords) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(words, that.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, words);
    }

    @Override
    public String toString() {
        return "CustomStopWords{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", words=" + words +
                "} " + super.toString();
    }
}
