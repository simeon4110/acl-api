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
 * Interfaces with spring security to extand the stock UserDetailsService.
 *
 * @author Josh Harkema
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private static final Logger logger = Logger.getLogger(UserDetailsServiceImpl.class);
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String USER_PRIV = "USER";
    private static final String ADMIN_PRIV = "ADMIN";

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PrivilegeRepository privilegeRepository) {
        this.userRepository = userRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(11);
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
        logger.debug("Adding user with details: " + userAddDto.toString());
        if (userRepository.findByUsername(userAddDto.getUsername()) != null) {
            logger.debug("User with username already exists: " + userAddDto.getUsername());
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
            logger.debug("User " + userModifyDto.getUsername() + " does not exist.");

            return "redirect:/admin/user/modify?notFound";
        } else {
            logger.debug("Modifying user: " + userModifyDto.getUsername());
            user = userRepository.findByUsername(userModifyDto.getUsername());
        }

        // Do reset password.
        if (userModifyDto.isResetPassword()) {
            if (Objects.equals(userModifyDto.getPasswordReset(), userModifyDto.getPasswordReset1())) {
                logger.debug("    Changing password...");
                user.setPassword(passwordEncoder.encode(userModifyDto.getPasswordReset()));
            } else {
                logger.debug("     Password change failed, no match.");

                return "redirect:/admin/user/modify?noMatch";
            }
        }

        // Add admin.
        if (userModifyDto.isAdmin() && !user.getPrivileges().contains(privilegeRepository.findByName(ADMIN_PRIV))) {
            logger.debug("Granting admin to: " + user.getUsername());
            user.getPrivileges().add(privilege);
        }

        // Remove admin.
        if (!userModifyDto.isAdmin() && user.getPrivileges().contains(privilegeRepository.findByName(ADMIN_PRIV))) {
            logger.debug("Removing admin rights from: " + user.getUsername());
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
            logger.debug("Password change successful.");

            return ("redirect:/profile?success");
        } else {
            logger.debug("Password change failed.");

            return ("redirect:/profile?error");
        }
    }
}
