package org.acl.database.persistence.dtos.web;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * @author Josh Harkema
 */
public class CustomStopWordsDto {
    private Long id;
    @NotEmpty
    private String name;
    private Set<String> words;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(required = true)
    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "CustomStopWordsDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", words=" + words.toString() +
                '}';
    }
}
