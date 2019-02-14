package com.sonnets.sonnet.persistence.dtos.web;

import com.sonnets.sonnet.persistence.models.web.Privilege;
import com.sonnets.sonnet.persistence.models.web.User;

import java.util.Set;

public class UserDetailsDto {
    private Long id;
    private String username;
    private String email;
    private Boolean isAdmin;
    private int requiredSonnets;
    private Boolean canConfirm;
    private Set<Privilege> privileges;

    public UserDetailsDto(final User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.isAdmin = user.getAdmin();
        this.requiredSonnets = user.getRequiredSonnets();
        this.canConfirm = user.getCanConfirm();
        this.privileges = user.getPrivileges();
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

    @Override
    public String toString() {
        return "UserDetailsDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", requiredSonnets=" + requiredSonnets +
                ", canConfirm=" + canConfirm +
                ", privileges=" + privileges +
                '}';
    }
}
