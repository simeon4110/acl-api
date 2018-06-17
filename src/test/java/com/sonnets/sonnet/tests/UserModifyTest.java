package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.dtos.user.UserAddDto;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import com.sonnets.sonnet.tools.TestUserDtoFactory;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class UserModifyTest {
    private static final Logger logger = Logger.getLogger(UserModifyTest.class);
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    public void testModifyUser() throws Exception {
        String username = UUID.randomUUID().toString();
        UserAddDto userAddDto = TestUserDtoFactory.generateUserDto(username);
        userDetailsService.addUser(userAddDto);

        // Admin array for testing admin user.
        final List<GrantedAuthority> adminAuthority = new ArrayList<>();
        adminAuthority.add(new SimpleGrantedAuthority("ADMIN"));
        adminAuthority.add(new SimpleGrantedAuthority("USER"));

        // Test changing from no admin to admin.
        logger.debug("Authorities: " + userDetailsService.loadUserByUsername(username).getAuthorities());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/modify")
                .with(user("admin").password("ToyCar11!").roles("ADMIN", "USER"))
                .param("username", username)
                .param("admin", "true")).andExpect(redirectedUrl("/admin/user/modify?success"));

        Assert.assertEquals(adminAuthority, userDetailsService.loadUserByUsername(username).getAuthorities());

        // Test password reset.
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/modify")
                .with(user("admin").password("ToyCar11!").roles("ADMIN", "USER"))
                .param("username", username).param("resetPassword", "true")
                .param("passwordReset", "Welcome11!").param("passwordReset1", "Welcome11!"))
                .andExpect(redirectedUrl("/admin/user/modify?success"));

        // Test mismatched passwords
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/modify")
                .with(user("admin").password("ToyCar11!").roles("ADMIN", "USER"))
                .param("username", username).param("resetPassword", "true")
                .param("passwordReset", "Welcome11!").param("passwordReset1", "Welcome1122"))
                .andExpect(redirectedUrl("/admin/user/modify?noMatch"));

    }

}
