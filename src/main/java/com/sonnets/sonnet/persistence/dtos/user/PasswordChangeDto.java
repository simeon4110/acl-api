package com.sonnets.sonnet.persistence.dtos.user;

import com.sonnets.sonnet.security.password.ValidPassword;

import javax.validation.constraints.NotEmpty;

/**
 * Simple POJO for passing password data from View to Controller.
 *
 * @author Josh Harkema
 */
public class PasswordChangeDto {
    @NotEmpty
    private String currentPassword;
    @NotEmpty
    @ValidPassword
    private String password;
    @NotEmpty
    @ValidPassword
    private String password1;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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

}
