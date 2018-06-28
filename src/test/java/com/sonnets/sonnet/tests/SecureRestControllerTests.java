package com.sonnets.sonnet.tests;

import com.google.gson.Gson;
import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.dtos.user.AdminPasswordResetDto;
import com.sonnets.sonnet.persistence.dtos.user.AdminUserAddDto;
import com.sonnets.sonnet.persistence.dtos.user.AdminUserDeleteDto;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.persistence.repositories.PrivilegeRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc testing for the secure REST endpoints used on the admin user management page.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class SecureRestControllerTests {
    private static final Logger LOGGER = Logger.getLogger(SecureRestControllerTests.class);
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(11);
    @Autowired
    private WebApplicationContext ctx;
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private SonnetDetailsService sonnetDetailsService;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    public void testSecureRestEndpoints() throws Exception {
        String username = UUID.randomUUID().toString();
        String newPassword = "Welcome11!";
        Principal testPrincipal = () -> "test_user";

        Gson gson = new Gson();

        AdminUserAddDto userAddDto = new AdminUserAddDto();
        userAddDto.setUsername(username);
        userAddDto.setEmail("test@test.com");
        userAddDto.setPassword("testPass234");
        userAddDto.setPassword1("testPass234");
        userAddDto.setAdmin(false);

        String newUser = gson.toJson(userAddDto);

        // Add a user.
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUser)).andExpect(status().isAccepted());
        Assert.assertEquals(username,
                userRepository.findByUsername(userAddDto.getUsername()).getUsername());

        // Test get by username
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/get/{username}", username)
                .principal(testPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        AdminPasswordResetDto resetDto = new AdminPasswordResetDto();
        resetDto.setUsername(username);
        resetDto.setPassword(newPassword);
        resetDto.setPassword1(newPassword);

        String resetPassword = gson.toJson(resetDto);

        // Reset a user's password.
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/user/modify/password")
                .contentType(MediaType.APPLICATION_JSON).content(resetPassword))
                .andExpect(status().isAccepted());

        // Verify password change.
        User user = userRepository.findByUsername(username);
        Assert.assertTrue(encoder.matches(newPassword, user.getPassword()));

        resetDto.setPassword1("blarkgaga");
        String badResetPassword = gson.toJson(resetDto);

        // Test bad password.
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/user/modify/password")
                .contentType(MediaType.APPLICATION_JSON).content(badResetPassword))
                .andExpect(status().isNotAcceptable());

        // Test adding invalid user.
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUser)).andExpect(status().isConflict());

        // Test modify privilege.
        Assert.assertTrue(!user.getPrivileges().contains(privilegeRepository.findByName("ADMIN")));

        AdminUserDeleteDto deleteDto = new AdminUserDeleteDto();
        deleteDto.setUsername(username);

        String deleteUser = gson.toJson(deleteDto);

        // Test delete user.
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/user/delete")
                .contentType(MediaType.APPLICATION_JSON).content(deleteUser))
                .andExpect(status().isAccepted());
        Assert.assertNull(userRepository.findByUsername(username));

        deleteDto.setUsername("9999999999999999");
        String badDeleteUser = gson.toJson(deleteDto);

        // Test delete bad user.
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/user/delete")
                .contentType(MediaType.APPLICATION_JSON).content(badDeleteUser))
                .andExpect(status().isNotAcceptable());

        // Test all users list endpoint.
        mockMvc.perform(get("/admin/user/all").principal(testPrincipal))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
    }
}
