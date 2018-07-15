package com.sonnets.sonnet.persistence.models;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Model object for corpera.
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "corpera")
public class Corpera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long userId;
    @Column
    private String name;
    @Column
    private String description;
    @ElementCollection
    private List<Sonnet> sonnets;

    public Corpera() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public List<Sonnet> getSonnets() {
        return sonnets;
    }

    public void setSonnets(List<Sonnet> sonnets) {
        this.sonnets = sonnets;
    }

    public void addSonnet(Sonnet sonnet) {
        this.sonnets.add(sonnet);
    }

    public void removeSonnet(Sonnet sonnet) {
        this.sonnets.remove(sonnet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Corpera corpera = (Corpera) o;
        return Objects.equals(id, corpera.id) &&
                Objects.equals(userId, corpera.userId) &&
                Objects.equals(name, corpera.name) &&
                Objects.equals(description, corpera.description) &&
                Objects.equals(sonnets, corpera.sonnets);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userId, name, description, sonnets);
    }

    @Override
    public String toString() {
        return "Corpera{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sonnets=" + sonnets +
                '}';
    }

}
