package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.user.*;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import com.sonnets.sonnet.services.UserItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * All endpoints related to users and the administrative tasks involving users are found here.
 *
 * @author Josh Harkema
 */
@RestController
public class UserController {
    private static final String ALLOWED_ORIGIN = "*";
    private final UserDetailsServiceImpl userDetailsService;
    private final UserItemsService userItemsService;

    @Autowired
    public UserController(UserDetailsServiceImpl userDetailsService, UserItemsService userItemsService) {
        this.userDetailsService = userDetailsService;
        this.userItemsService = userItemsService;
    }

    /**
     * @return a json formatted list with all the user's and their associated data.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/secure/user/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        return userDetailsService.getAllUsers();
    }

    /**
     * Handles PUT requests to reset a user's password.
     *
     * @param resetDto a valid dto with the new password.
     * @return HttpStatus.ACCEPTED on success; HttpStatus.NOT_ACCEPTABLE on failure.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/user/modify_password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid AdminPasswordResetDto resetDto) {
        return userDetailsService.adminPasswordReset(resetDto.getUsername(), resetDto.getPassword(),
                resetDto.getPassword1());
    }

    /**
     * Handles put requests to update a user's admin status.
     *
     * @param modifyUserDto the dto valid dto with the new user data.
     * @return HttpStatus.NOT_ACCEPTABLE if username does not exist; HttpStatus.ACCEPTED if successful.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/user/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyAdmin(@RequestBody @Valid AdminUserModifyDto modifyUserDto) {
        return userDetailsService.adminModifyUser(modifyUserDto.getUsername(), modifyUserDto.getEmail(),
                modifyUserDto.isAdmin());
    }

    /**
     * Delete a user.
     *
     * @param username the name of the user to delete.
     * @return NOT_ACCEPTABLE if user does not exist; ACCEPTED if success.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/user/delete/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        return userDetailsService.adminDeleteUser(username);
    }

    /**
     * Add a new user via a POST request.
     *
     * @param userAddDto a valid user dto object parsed from the request body.
     * @return HttpStatus.CONFLICT if username already exists; HttpStatus.NOT_ACCEPTABLE if passwords don't match;
     * HttpStatus.ACCEPTED if successful.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/secure/user/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid AdminUserAddDto userAddDto) {
        return userDetailsService.adminAddUser(userAddDto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PostMapping(value = "/secure/user/guest_registration", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addGuest(@RequestBody @Valid GuestUserAddDto guestUserAddDto) {
        return userDetailsService.guestUserAdd(guestUserAddDto);
    }

    /**
     * Returns a specific user's details.
     *
     * @param username  the username to lookup.
     * @param principal the principal from the request object.
     * @return the user object.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/user/details/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User userGetOwnDetails(@PathVariable String username, Principal principal) {
        return userDetailsService.loadUserObjectByUsernameMatchUsername(username, principal);
    }

    /**
     * Endpoint allows a user to change their own password.
     *
     * @param passwordChangeDto a valid dto for the password change.
     * @param principal         the principal from the request.
     * @return http.ok if the request is good.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @PutMapping(value = "/secure/user/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto,
                                              Principal principal) {
        return userDetailsService.userUpdatePassword(principal, passwordChangeDto);
    }

    /**
     * Allows a user to reset their own email.
     *
     * @param emailChangeDto valid dto for the email change.
     * @param principal      the principal from the request.
     * @return http.ok if the request is good.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @PutMapping(value = "/secure/user/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeEmail(@RequestBody @Valid EmailChangeDto emailChangeDto,
                                            Principal principal) {
        return userDetailsService.userUpdateEmail(principal, emailChangeDto);
    }

    /**
     * Gets all items added by a user.
     *
     * @param principal the request principal.
     * @return a list of all items added by a user.
     */
    @Cacheable(value = "user-items-all", key = "#principal.name")
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/secure/user/get_items", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserItems(Principal principal) {
        return userItemsService.getUserItems(principal).join();
    }
}
