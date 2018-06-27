package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.persistence.repositories.PrivilegeRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.SonnetGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Add a user.
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/add")
                .param("username", username).param("password", newPassword)
                .param("password1", newPassword).param("isAdmin", "false")
                .principal(testPrincipal)).andExpect(status().isAccepted());
        Assert.assertEquals(username, userRepository.findByUsername(username).getUsername());

        // Test get by username
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/get/{username}", username).principal(testPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // Reset a user's password.
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/user/modify/password")
                .param("username", username).param("password", newPassword)
                .param("password1", newPassword).principal(testPrincipal))
                .andExpect(status().isAccepted());

        // Verify password change.
        User user = userRepository.findByUsername(username);
        Assert.assertTrue(encoder.matches(newPassword, user.getPassword()));

        // Test bad password.
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/user/modify/password")
                .param("username", username).param("password", newPassword)
                .param("password1", "gibberish").principal(testPrincipal))
                .andExpect(status().isNotAcceptable());

        // Test adding invalid user.
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/add").param("username", username)
                .param("password", newPassword).param("password1", newPassword)
                .param("isAdmin", "false").principal(testPrincipal))
                .andExpect(status().isConflict());

        // Test modify privilege.
        Assert.assertTrue(!user.getPrivileges().contains(privilegeRepository.findByName("ADMIN")));
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/user/modify/admin")
                .param("username", username).param("isAdmin", "true").principal(testPrincipal))
                .andExpect(status().isAccepted());
        user = userRepository.findByUsername(username);
        Assert.assertTrue(user.getPrivileges().contains(privilegeRepository.findByName("ADMIN")));

        // Test delete user.
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/user/delete")
                .param("username", user.getUsername()).principal(testPrincipal))
                .andExpect(status().isAccepted());
        Assert.assertNull(userRepository.findByUsername(username));

        // Test delete bad user.
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/user/delete")
                .param("username", "99999999999999").principal(testPrincipal))
                .andExpect(status().isNotAcceptable());

        // Test delete sonnet.
        Sonnet sonnet = sonnetDetailsService.addNewSonnet(SonnetGenerator.sonnetGenerator());
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/sonnet/delete")
                .param("id", sonnet.getId().toString()).principal(testPrincipal))
                .andExpect(status().isAccepted());
        Assert.assertNull(sonnetDetailsService.getSonnetByID(sonnet.getId().toString()));

        // Test delete non-existent sonnet.
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/sonnet/delete")
                .param("id", "999999999999").principal(testPrincipal))
                .andExpect(status().isNotAcceptable());

        // Test bad id value.
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/sonnet/delete")
                .param("id", "test").principal(testPrincipal))
                .andExpect(status().isBadRequest());

        // Test all users list endpoint.
        mockMvc.perform(get("/admin/user/all").principal(testPrincipal))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
    }
}
