package com.sonnets.sonnet.persistence.dtos.corpera;

import javax.validation.constraints.NotEmpty;

/**
 * POJO for modifying corpera.
 *
 * @author Josh Harkema
 */
public class CorperaModifyDto {
    @NotEmpty
    private String corperaId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;

    public String getCorperaId() {
        return corperaId;
    }

    public void setCorperaId(String corperaId) {
        this.corperaId = corperaId;
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

    @Override
    public String toString() {
        return "CorperaModifyDto{" +
                "corperaId='" + corperaId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
