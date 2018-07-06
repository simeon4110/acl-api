package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.dtos.user.*;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * The secure REST controller for the admin user management page.
 *
 * @author Josh Harkema
 */
@RestController
public class SecureRestController {
    private static final Logger LOGGER = Logger.getLogger(SecureRestController.class);
    private final UserDetailsServiceImpl userDetailsService;
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public SecureRestController(UserDetailsServiceImpl userDetailsService,
                                SonnetDetailsService sonnetDetailsService) {
        this.userDetailsService = userDetailsService;
        this.sonnetDetailsService = sonnetDetailsService;
    }

    /**
     * Returns a specific user's details.
     *
     * @param username the user to return.
     * @return a user object.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/admin/user/get/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable String username) {
        LOGGER.debug("Retrieving user with username: " + username);

        return userDetailsService.loadUserObjectByUsername(username);
    }

    /**
     * @return a json formatted list with all the user's and their associated data.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/admin/user/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllUsers() {
        LOGGER.debug("Returning a list of all users.");

        return userDetailsService.getAllUsers();
    }

    /**
     * Handles PUT requests to reset a user's password.
     *
     * @param resetDto a valid dto with the new password.
     * @return HttpStatus.ACCEPTED on success; HttpStatus.NOT_ACCEPTABLE on failure.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/admin/user/modify/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyUser(@RequestBody @Valid AdminPasswordResetDto resetDto) {
        LOGGER.debug("Admin reset user password for user: " + resetDto.getUsername());

        return userDetailsService.adminPasswordReset(resetDto.getUsername(), resetDto.getPassword(),
                resetDto.getPassword1());
    }

    /**
     * Handles put requests to update a user's admin status.
     *
     * @param modifyUserDto the dto vaid dto with the new user data.
     * @return HttpStatus.NOT_ACCEPTABLE if username does not exist; HttpStatus.ACCEPTED if successful.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/admin/user/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyUserAdmin(@RequestBody @Valid AdminUserModifyDto modifyUserDto) {
        LOGGER.debug("Changing admin status for user: " + modifyUserDto.getUsername());

        return userDetailsService.adminModifyUser(modifyUserDto.getUsername(), modifyUserDto.getEmail(),
                modifyUserDto.isAdmin());
    }

    /**
     * Add a new user via a POST request.
     *
     * @param userAddDto a valid user dto object parsed from the request body.
     * @return HttpStatus.CONFLICT if username already exists; HttpStatus.NOT_ACCEPTABLE if passwords don't match;
     * HttpStatus.ACCEPTED if successful.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/admin/user/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addUserRest(@RequestBody @Valid AdminUserAddDto userAddDto) {
        LOGGER.debug("Adding new user with username: " + userAddDto.getUsername());

        return userDetailsService.adminAddUser(userAddDto.getUsername(), userAddDto.getEmail(),
                userAddDto.getPassword(), userAddDto.getPassword1(), userAddDto.getAdmin());
    }

    /**
     * Handles AJAX post requests and returns the report data.
     *
     * @param reportDto a valid AdminReportDto parsed from the JSON request body.
     * @return a JSON list of any results.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/admin/reports/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> generateReport(@RequestBody @Valid AdminReportDto reportDto) {

        return sonnetDetailsService.getSonnetsByAddedByAndDate(reportDto.getUsername(), reportDto.getAfter(),
                reportDto.getBefore());
    }

    /**
     * Delete a sonnet.
     *
     * @param id the sonnet's id.
     * @return BAD_REQUEST if number is invalid; ACCEPTED if success; NOT_ACCEPTABLE if sonnet does not exist.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/admin/sonnet/delete/{id}")
    public ResponseEntity<Void> deleteSonnet(@PathVariable("id") String id) {
        LOGGER.debug("Deleting sonnet with ID: " + id);

        return sonnetDetailsService.deleteSonnetById(id);
    }

    /**
     * Delete a user.
     *
     * @param deleteDto the dto with the user to delete.
     * @return NOT_ACCEPTABLE if user does not exist; ACCEPTED if success.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/admin/user/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(@RequestBody @Valid AdminUserDeleteDto deleteDto) {
        LOGGER.debug("Deleting user: " + deleteDto.getUsername());

        return userDetailsService.adminDeleteUser(deleteDto.getUsername());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secured/sonnet/add")
    public ResponseEntity<Void> addSonnet(@RequestBody @Valid SonnetDto sonnetDto) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
