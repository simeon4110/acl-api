package com.sonnets.sonnet.persistence.dtos.corpera;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO POJO for creating a new Corpera.
 *
 * @author Josh Harkema
 */
public class CorperaDto {
    @NotNull
    @NotEmpty
    private String name;

    private String description;

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

    @Override
    public String toString() {
        return "CorperaDto{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
