package com.sonnets.sonnet.security;

import com.sonnets.sonnet.persistence.dtos.user.PasswordChangeDto;
import com.sonnets.sonnet.persistence.dtos.user.UserAddDto;
import com.sonnets.sonnet.persistence.dtos.user.UserModifyDto;
import com.sonnets.sonnet.persistence.models.Privilege;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.persistence.repositories.PrivilegeRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Interfaces with spring security to extend the stock UserDetailsService. Allows custom login page.
 *
 * @author Josh Harkema
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = Logger.getLogger(UserDetailsServiceImpl.class);

    private static final String USER_PRIV = "USER";
    private static final String ADMIN_PRIV = "ADMIN";
    private static final int ENCODER_STRENGTH = 11;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PrivilegeRepository privilegeRepository) {
        this.userRepository = userRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(ENCODER_STRENGTH);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserPrincipalImpl(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Add a new user to the database.
     *
     * @param userAddDto the UserAddDto with the form data.
     * @return an error if the user exists, or a success message on success.
     */
    public String addUser(final UserAddDto userAddDto) {
        // Return error if username already exists.
        LOGGER.debug("Adding user with details: " + userAddDto.toString());
        if (userRepository.findByUsername(userAddDto.getUsername()) != null) {
            LOGGER.debug("User with username already exists: " + userAddDto.getUsername());
            return "redirect:/admin?exists";
        }

        User user = new User();
        user.setUsername(userAddDto.getUsername());

        if (Objects.equals(userAddDto.getPassword(), userAddDto.getPassword1())) {
            user.setPassword(passwordEncoder.encode(userAddDto.getPassword()));
        }

        Set<Privilege> privileges = new HashSet<>();
        privileges.add(privilegeRepository.findByName(USER_PRIV));
        if (userAddDto.isAdmin()) {
            privileges.add(privilegeRepository.findByName(ADMIN_PRIV));
        }
        user.setPrivileges(privileges);

        userRepository.saveAndFlush(user);

        return "redirect:/admin?success";
    }

    /**
     * Modify an existing user.
     *
     * @param userModifyDto valid UserModifyDto
     * @return error / success redirect.
     */
    public String modifyUser(final UserModifyDto userModifyDto) {
        User user;
        Privilege privilege = privilegeRepository.findByName(ADMIN_PRIV);

        // Check if user exists.
        if (userRepository.findByUsername(userModifyDto.getUsername()) == null) {
            LOGGER.debug("User " + userModifyDto.getUsername() + " does not exist.");

            return "redirect:/admin/user/modify?notFound";
        } else {
            LOGGER.debug("Modifying user: " + userModifyDto.getUsername());
            user = userRepository.findByUsername(userModifyDto.getUsername());
        }

        // Do reset password.
        if (userModifyDto.isResetPassword()) {
            if (Objects.equals(userModifyDto.getPasswordReset(), userModifyDto.getPasswordReset1())) {
                LOGGER.debug("    Changing password...");
                user.setPassword(passwordEncoder.encode(userModifyDto.getPasswordReset()));
            } else {
                LOGGER.debug("     Password change failed, no match.");

                return "redirect:/admin/user/modify?noMatch";
            }
        }

        // Do delete user.
        if (userModifyDto.isDelete()) {
            userRepository.delete(userRepository.findByUsername(userModifyDto.getUsername()));

            return "redirect:/admin/user/modify?deleted";
        }

        // Add admin.
        if (userModifyDto.isAdmin() && !user.getPrivileges().contains(privilegeRepository.findByName(ADMIN_PRIV))) {
            LOGGER.debug("Granting admin to: " + user.getUsername());
            user.getPrivileges().add(privilege);
        }

        // Remove admin.
        if (!userModifyDto.isAdmin() && user.getPrivileges().contains(privilegeRepository.findByName(ADMIN_PRIV))) {
            LOGGER.debug("Removing admin rights from: " + user.getUsername());
            user.getPrivileges().remove(privilege);
        }

        userRepository.save(user);

        return "redirect:/admin/user/modify?success";
    }

    /**
     * Update a user's password. Verifies the old password is correct and ensures both password and password1 match.
     *
     * @param principal         the security principal with the user's auth details.
     * @param passwordChangeDto the PasswordChangeDto with the form data.
     * @return a success message if successful, or a error message if not.
     */
    public String updatePassword(final Principal principal, final PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findByUsername(principal.getName());

        if (passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword()) &&
                Objects.equals(passwordChangeDto.getPassword(), passwordChangeDto.getPassword1())) {
            user.setPassword(passwordEncoder.encode(passwordChangeDto.getPassword()));
            userRepository.save(user);
            LOGGER.debug("Password change successful.");

            return ("redirect:/profile?success");
        } else {
            LOGGER.debug("Password change failed.");

            return ("redirect:/profile?error");
        }
    }
}
