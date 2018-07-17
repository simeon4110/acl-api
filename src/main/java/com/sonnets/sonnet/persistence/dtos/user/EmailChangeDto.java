package com.sonnets.sonnet.persistence.dtos.user;

import javax.validation.constraints.Email;

/**
 * POJO for handling email change forms.
 *
 * @author Josh Harkema
 */
public class EmailChangeDto {
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmailChangeDto{" +
                ", email='" + email + '\'' +
                '}';
    }
}
