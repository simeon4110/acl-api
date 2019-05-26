package com.sonnets.sonnet.persistence.dtos.web;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;

/**
 * DTO POJO for creating a new Corpora.
 *
 * @author Josh Harkema
 */
public class CorporaDto {
    private Long id;
    @NotEmpty
    private String name;

    private String description;

    private String[] itemIds;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getItemIds() {
        return itemIds;
    }

    public void setItemIds(String[] itemIds) {
        this.itemIds = itemIds;
    }

    @Override
    public String toString() {
        return "CorporaDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", itemIds=" + Arrays.toString(itemIds) +
                '}';
    }
}
