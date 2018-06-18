package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.dtos.user.PasswordChangeDto;
import com.sonnets.sonnet.persistence.dtos.user.UserAddDto;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import com.sonnets.sonnet.tools.TestUserDtoFactory;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.UUID;

/**
 * Ensures password reset functions as expected on the backend.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class UserPasswordTest {
    private static final Logger LOGGER = Logger.getLogger(UserPasswordTest.class);
    private static final String OLD_PASSWORD = "password1";
    private static final String NEW_PASSWORD = "password2";
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(11);
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void changePasswordAndVerify() {
        String username = UUID.randomUUID().toString();
        UserAddDto userAddDto = TestUserDtoFactory.generateUserDto(username);
        userDetailsService.addUser(userAddDto);

        LOGGER.debug("Verifying password change for user: " + username);

        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setCurrentPassword(OLD_PASSWORD);
        passwordChangeDto.setPassword(NEW_PASSWORD);
        passwordChangeDto.setPassword1(NEW_PASSWORD);

        userDetailsService.updatePassword(() -> username, passwordChangeDto);
        Assert.assertTrue(encoder.matches(NEW_PASSWORD, userRepository.findByUsername(username).getPassword()));
        LOGGER.debug("Assertion [] password change is valid.");
    }

}
