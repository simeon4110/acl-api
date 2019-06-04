package org.acl.database.persistence.dtos.web;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;

/**
 * @author Josh Harkema
 */
public class CustomStopWordsDto {
    private Long id;
    @NotEmpty
    private String name;
    private String[] words;

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

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "CustomStopWordsDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", words=" + Arrays.toString(words) +
                '}';
    }
}
