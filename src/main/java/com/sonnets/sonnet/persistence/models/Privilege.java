package com.sonnets.sonnet.persistence.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Privilege object to generate user roles. Currently only "USER" and "ADMIN" exist.
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "privileges")
public class Privilege implements Serializable {
    private static final long serialVersionUID = 3568710389315424523L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Privilege() {
        super();
    }

    public Privilege(String name) {
        super();
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Privilege privilege = (Privilege) o;

        if (id != null ? !id.equals(privilege.id) : privilege.id != null) return false;
        return name != null ? name.equals(privilege.name) : privilege.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Privilege{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
