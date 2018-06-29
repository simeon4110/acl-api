package com.sonnets.sonnet.security;

import com.sonnets.sonnet.persistence.dtos.user.PasswordChangeDto;
import com.sonnets.sonnet.persistence.models.Privilege;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.persistence.repositories.PrivilegeRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.services.EmailServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
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
    private final EmailServiceImpl emailService;
    private static final Logger LOGGER = Logger.getLogger(UserDetailsServiceImpl.class);

    private static final String USER_PRIVILEGE = "USER";
    private static final String ADMIN_PRIVILEGE = "ADMIN";
    private static final int ENCODER_STRENGTH = 11;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PrivilegeRepository privilegeRepository,
                                  EmailServiceImpl emailService) {
        this.userRepository = userRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(ENCODER_STRENGTH);
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserPrincipalImpl(user);
    }

    public User loadUserObjectByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return user;
    }

    /**
     * @return a list of all users in the database.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Update a user's password. Verifies the old password is correct and ensures both password and password1 match.
     *
     * @param principal         the security principal with the user's auth details.
     * @param passwordChangeDto the PasswordChangeDto with the form data.
     * @return a success message if successful, or a error message if not.
     */
    public String userUpdatePassword(final Principal principal, final PasswordChangeDto passwordChangeDto) {
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

    /**
     * Allows administrator to reset a user's password.
     *
     * @param username  the user to reset the password for.
     * @param password  the new password.
     * @param password1 new password confirm.
     * @return NOT_ACCEPTABLE if passwords don't match; ACCEPTED if successful.
     */
    public ResponseEntity<Void> adminPasswordReset(final String username, final String password,
                                                   final String password1) {
        LOGGER.debug("Admin password reset for user: " + username);
        User user = userRepository.findByUsername(username);

        if (Objects.equals(password, password1)) {
            user.setPassword(passwordEncoder.encode(password));
            LOGGER.debug("Password change successful.");
            userRepository.saveAndFlush(user);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {

            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * Allows admins to delete a user.
     *
     * @param username the user to delete.
     * @return NOT_ACCEPTABLE if user is not found; ACCEPTED if successful.
     */
    public ResponseEntity<Void> adminDeleteUser(final String username) {
        LOGGER.debug("Deleting user: " + username);
        User user = userRepository.findByUsername(username);

        if (user == null) {
            LOGGER.debug(username + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        userRepository.delete(user);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Allows admins to add a new user.
     *
     * @param username  the new user's desired username.
     * @param password  a new password.
     * @param password1 validate new password.
     * @param isAdmin   True = admin privileges.
     * @return CONFLICT if username in use; NOT_ACCEPTABLE if passwords don't match; ACCEPTED if successful.
     */
    public ResponseEntity<Void> adminAddUser(final String username, final String email, final String password,
                                             final String password1, final boolean isAdmin) {
        LOGGER.debug("Adding new Rest user with username: " + username);

        // Return conflict if username already exits.
        if (userRepository.findByUsername(username) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Return not acceptable if passwords don't match.
        if (!Objects.equals(password, password1)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // Create and save a new user, return status accepted.
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAddedAt(new Timestamp(System.currentTimeMillis()));
        Set<Privilege> privileges = new HashSet<>();
        privileges.add(privilegeRepository.findByName(USER_PRIVILEGE));
        user.setAdmin(false);
        if (isAdmin) {
            privileges.add(privilegeRepository.findByName(ADMIN_PRIVILEGE));
            user.setAdmin(true);
        }
        user.setPrivileges(privileges);
        userRepository.saveAndFlush(user);

        // Send account details to new user.
        sendEmailInvite(user.getEmail(), username, password);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Allows an admin to change a user's admin privileges.
     *
     * @param username the user to change.
     * @param isAdmin  true = has admin rights.
     * @return NOT_ACCEPTABLE if user name is not found; ACCEPTED if successful.
     */
    public ResponseEntity<Void> adminModifyUser(final String username, final String email, final boolean isAdmin) {
        LOGGER.debug("Setting user " + username + " to admin = " + isAdmin + " with email " + email);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        if (!isAdmin && user.getPrivileges().contains(privilegeRepository.findByName(ADMIN_PRIVILEGE))) {
            user.getPrivileges().remove(privilegeRepository.findByName(ADMIN_PRIVILEGE));
            user.setAdmin(false);
            userRepository.saveAndFlush(user);
        }

        if (isAdmin && !user.getPrivileges().contains(privilegeRepository.findByName(ADMIN_PRIVILEGE))) {
            user.getPrivileges().add(privilegeRepository.findByName(ADMIN_PRIVILEGE));
            user.setAdmin(true);
            userRepository.saveAndFlush(user);
        }

        if (!Objects.equals(user.getEmail(), email)) {
            user.setEmail(email);
            userRepository.saveAndFlush(user);
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Sends an invite email to a new user.
     *
     * @param to       the email address of the new user.
     * @param username the user's new username.
     * @param password the user's password.
     */
    private void sendEmailInvite(String to, String username, String password) {
        LOGGER.debug("Sending invite email to: " + to);
        String message = "Your ACL database credentials are:" +
                "\nusername: " + username +
                "\npassword: " + password +
                "\n\nYou should reset your password by selecting 'profile' when you log in for the first time.";

        String subject = "ACL Database User Account";

        emailService.sendSimpleMessage(to, subject, message);
    }
}
