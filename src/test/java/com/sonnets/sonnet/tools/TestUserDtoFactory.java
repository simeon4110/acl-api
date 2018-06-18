package com.sonnets.sonnet.tools;


import com.sonnets.sonnet.persistence.dtos.user.UserAddDto;

/**
 * Generates a random UserDto for testing.
 *
 * @author Josh Harkema
 */
public abstract class TestUserDtoFactory {
    private static final String PASSWORD = "password1";

    private TestUserDtoFactory() {
    }

    public static UserAddDto generateUserDto(String username) {
        UserAddDto userAddDto = new UserAddDto();
        userAddDto.setUsername(username);
        userAddDto.setPassword(PASSWORD);
        userAddDto.setPassword1(PASSWORD);
        userAddDto.setAdmin(false);

        return userAddDto;
    }
}
