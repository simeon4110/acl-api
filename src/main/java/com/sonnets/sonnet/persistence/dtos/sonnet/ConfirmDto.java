package com.sonnets.sonnet.persistence.dtos.sonnet;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ConfirmDto {
    @NotNull
    private Long id;
    @NotEmpty
    private String user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ConfirmDto{" +
                "id=" + id +
                ", user='" + user + '\'' +
                '}';
    }
}
