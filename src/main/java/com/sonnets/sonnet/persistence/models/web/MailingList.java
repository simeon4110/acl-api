package com.sonnets.sonnet.persistence.models.web;

import org.hibernate.search.annotations.DocumentId;

import javax.persistence.*;
import java.util.Objects;

/**
 * For storing users who want to join the mailing list.
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "mailing_list")
public class MailingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Column
    private String name;
    @Column
    private String email;

    public MailingList() {
        // Default constructor for spring data.
    }

    public MailingList(String name, String email) {
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailingList that = (MailingList) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, email);
    }

    @Override
    public String toString() {
        return "MailingList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
