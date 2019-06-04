package org.acl.database.persistence.dtos.web;

public class CorporaBasicOutDto {
    private Long id;
    private String name;
    private String description;
    private int totalItems;

    public CorporaBasicOutDto(Long id, String name, String description, int totalItems) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalItems = totalItems;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    @Override
    public String toString() {
        return "CorporaBasicOutDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", totalItems=" + totalItems +
                '}';
    }
}
