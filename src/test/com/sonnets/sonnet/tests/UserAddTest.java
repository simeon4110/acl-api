package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.dtos.user.UserAddDto;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.persistence.repositories.PrivilegeRepository;
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
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class UserAddTest {
    private static final Logger logger = Logger.getLogger(UserAddTest.class);
    private static final String PASSWORD = "password1";
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(11);
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Test
    public void verifyUser() {
        String username = UUID.randomUUID().toString();
        UserAddDto userAddDto = TestUserDtoFactory.generateUserDto(username);
        userDetailsService.addUser(userAddDto);
        User user = userRepository.findByUsername(username);

        logger.debug("Verifying user with details: " + user.toString());

        Assert.assertEquals(username, user.getUsername());
        logger.debug("Assertion [] username is valid.");
        Assert.assertTrue(encoder.matches(PASSWORD, user.getPassword()));
        logger.debug("Assertion [] passwords match.");
        Assert.assertTrue(user.getPrivileges().contains(privilegeRepository.findByName("USER")));
        logger.debug("Assertion [] user has USER privilege.");
        Assert.assertFalse(user.getPrivileges().contains(privilegeRepository.findByName("ADMIN")));
        logger.debug("Assertion [] user does not have ADMIN privilege.");
    }

    @Test
    public void verifyUsernameCheck() {
        String username = UUID.randomUUID().toString();
        UserAddDto original = TestUserDtoFactory.generateUserDto(username);
        userDetailsService.addUser(original);

        logger.debug("Verifying a username can only be used once... ");

        UserAddDto shouldFail = TestUserDtoFactory.generateUserDto(username);
        Assert.assertEquals("redirect:/admin?exists", userDetailsService.addUser(shouldFail));
        logger.debug("Assertion [] username can only be used once passed.");
    }

}
