package com.sonnets.sonnet.security;

import com.sonnets.sonnet.persistence.dtos.PasswordChangeDto;
import com.sonnets.sonnet.persistence.dtos.UserAddDto;
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
        privileges.add(privilegeRepository.findByName("USER"));
        if (userAddDto.isAdmin()) {
            privileges.add(privilegeRepository.findByName("ADMIN"));
        }
        user.setPrivileges(privileges);

        userRepository.saveAndFlush(user);

        return "redirect:/admin?success";
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
