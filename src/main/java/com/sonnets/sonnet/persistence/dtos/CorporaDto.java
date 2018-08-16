package com.sonnets.sonnet.persistence.dtos;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;

/**
 * DTO POJO for creating a new Corpora.
 *
 * @author Josh Harkema
 */
public class CorporaDto {
    private String id;
    @NotEmpty
    private String name;

    private String description;

    private String[] sonnetIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getSonnetIds() {
        return sonnetIds;
    }

    public void setSonnetIds(String[] sonnetIds) {
        this.sonnetIds = sonnetIds;
    }

    @Override
    public String toString() {
        return "CorporaDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sonnetIds=" + Arrays.toString(sonnetIds) +
                '}';
    }
}
