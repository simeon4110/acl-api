package com.sonnets.sonnet.persistence.models.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sonnets.sonnet.persistence.models.base.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * User model for storing auth info and user details.
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "users")
public class User extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = 5508512949127227678L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @JsonIgnore
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private Boolean isAdmin;
    @Column
    private int requiredSonnets;
    @Column
    private Boolean canConfirm;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_privileges", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<Privilege> privileges;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "id")
    private List<UserPrivateText> privateTexts;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "id")
    private List<CustomStopWords> customStopWords;
    @ElementCollection
    private List<Long> testingSonnets;
    @ElementCollection
    private List<Integer> testingIndexes;
    @Column
    private int currentIndex;

    public User() {
        super();
        this.currentIndex = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public int getRequiredSonnets() {
        return requiredSonnets;
    }

    public void setRequiredSonnets(int requiredSonnets) {
        this.requiredSonnets = requiredSonnets;
    }

    public Boolean getCanConfirm() {
        return canConfirm;
    }

    public void setCanConfirm(Boolean canConfirm) {
        this.canConfirm = canConfirm;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public List<UserPrivateText> getPrivateTexts() {
        return privateTexts;
    }

    public void setPrivateTexts(List<UserPrivateText> privateTexts) {
        this.privateTexts = privateTexts;
    }

    public List<CustomStopWords> getCustomStopWords() {
        return customStopWords;
    }

    public void setCustomStopWords(List<CustomStopWords> customStopWords) {
        this.customStopWords = customStopWords;
    }

    public List<Long> getTestingSonnets() {
        return testingSonnets;
    }

    public void setTestingSonnets(List<Long> testingSonnets) {
        this.testingSonnets = testingSonnets;
    }

    public List<Integer> getTestingIndexes() {
        return testingIndexes;
    }

    public void setTestingIndexes(List<Integer> testingIndexes) {
        this.testingIndexes = testingIndexes;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return requiredSonnets == user.requiredSonnets &&
                Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(isAdmin, user.isAdmin) &&
                Objects.equals(canConfirm, user.canConfirm) &&
                Objects.equals(privileges, user.privileges) &&
                Objects.equals(privateTexts, user.privateTexts) &&
                Objects.equals(customStopWords, user.customStopWords) &&
                Objects.equals(testingSonnets, user.testingSonnets) &&
                Objects.equals(testingIndexes, user.testingIndexes) &&
                Objects.equals(currentIndex, user.currentIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, username, password, email, isAdmin, requiredSonnets, canConfirm,
                privileges, privateTexts, customStopWords, testingSonnets, testingIndexes, currentIndex);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", requiredSonnets=" + requiredSonnets +
                ", canConfirm=" + canConfirm +
                ", privileges=" + privileges +
                ", privateTexts=" + privateTexts +
                ", customStopWords=" + customStopWords +
                ", testingSonnets=" + testingSonnets +
                ", testingIndexes=" + testingIndexes +
                ", currentIndex=" + currentIndex +
                "} " + super.toString();
    }
}