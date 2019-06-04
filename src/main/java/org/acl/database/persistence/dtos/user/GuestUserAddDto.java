package org.acl.database.persistence.dtos.user;

import org.acl.database.security.password.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * DTO for guest user registration.
 *
 * @author Josh Harkema
 */
public class GuestUserAddDto {
    @NotEmpty
    private String username;
    @Email
    private String email;
    @ValidPassword
    private String password;
    @NotEmpty
    private String password1;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    @Override
    public String toString() {
        return "GuestUserAddDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", password1='" + password1 + '\'' +
                '}';
    }

}
