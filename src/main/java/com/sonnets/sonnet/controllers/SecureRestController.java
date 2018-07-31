package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.persistence.dtos.corpera.CorperaDto;
import com.sonnets.sonnet.persistence.dtos.corpera.CorperaModifyDto;
import com.sonnets.sonnet.persistence.dtos.corpera.CorperaSonnetDto;
import com.sonnets.sonnet.persistence.dtos.sonnet.ConfirmDto;
import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.dtos.user.*;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import com.sonnets.sonnet.services.CorperaService;
import com.sonnets.sonnet.services.MessageService;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
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
    private final MessageService messageService;
    private static final String ALLOWED_ORIGIN = "*";
    private final CorperaService corperaService;

    @Autowired
    public SecureRestController(UserDetailsServiceImpl userDetailsService,
                                SonnetDetailsService sonnetDetailsService,
                                CorperaService corperaService, MessageService messageService) {
        this.userDetailsService = userDetailsService;
        this.sonnetDetailsService = sonnetDetailsService;
        this.corperaService = corperaService;
        this.messageService = messageService;
    }

    // ADMIN ENDPOINTS:

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/admin/user/get_by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserById(@PathVariable String id) {
        LOGGER.debug("Retrieving user with id: " + id);

        return userDetailsService.loadUserById(id);
    }

    /**
     * Returns a specific user's details.
     *
     * @param username the user to return.
     * @return a user object.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/admin/user/get_by_username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable String username) {
        LOGGER.debug("Retrieving user with username: " + username);

        return userDetailsService.loadUserObjectByUsername(username);
    }

    /**
     * @return a json formatted list with all the user's and their associated data.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
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
    @CrossOrigin(origins = ALLOWED_ORIGIN)
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
    @CrossOrigin(origins = ALLOWED_ORIGIN)
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
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/admin/user/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addUser(@RequestBody @Valid AdminUserAddDto userAddDto) {
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
    @CrossOrigin(origins = ALLOWED_ORIGIN)
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
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/admin/sonnet/delete/{id}")
    public ResponseEntity<Void> deleteSonnet(@PathVariable("id") String id) {
        LOGGER.debug("Deleting sonnet with ID: " + id);

        return sonnetDetailsService.deleteSonnetById(id);
    }

    /**
     * Delete a user.
     *
     * @param username the name of the user to delete.
     * @return NOT_ACCEPTABLE if user does not exist; ACCEPTED if success.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/admin/user/delete/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        LOGGER.debug("Deleting user: " + username);

        return userDetailsService.adminDeleteUser(username);
    }

    // SONNET ENDPOINTS:

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/sonnet/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addSonnet(@RequestBody @Valid SonnetDto sonnetDto) {
        LOGGER.debug("Adding sonnet: " + sonnetDto.toString());

        return sonnetDetailsService.addNewSonnet(sonnetDto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/sonnet/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> confirmSonnet(@RequestBody @Valid ConfirmDto confirmDto) {

        return sonnetDetailsService.confirmSonnet(confirmDto);
    }

    // USER ENDPOINTS:

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/user/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto,
                                              Principal principal) {
        return userDetailsService.userUpdatePassword(principal, passwordChangeDto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/user/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeEmail(@RequestBody @Valid EmailChangeDto emailChangeDto,
                                            Principal principal) {
        return userDetailsService.userUpdateEmail(principal, emailChangeDto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/user/details/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User userGetOwnDetails(@PathVariable String username, Principal principal) {

        return userDetailsService.loadUserObjectByUsernameMatchUsername(username, principal);
    }

    // MESSAGE ENDPOINTS:

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/user/get_inbox", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getMessageInbox(Principal principal) {
        return messageService.getMessagesTo(principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/user/get_outbox", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getMessageOutbox(Principal principal) {
        return messageService.getMessagesFrom(principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/secure/user/send_message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid MessageDto messageDto, Principal principal) {
        return messageService.sendMessage(principal, messageDto);
    }

    // CORPERA ENDPOINTS:

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/corpera/my_corpera", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserCorpera(Principal principal) {
        return corperaService.getUserCorpera(principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/secure/corpera/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCorpera(@RequestBody @Valid CorperaDto corperaDto, Principal principal) {
        return corperaService.createCorpera(corperaDto, principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN, methods = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/corpera/add_sonnet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addSonnetToCorpera(@RequestBody @Valid CorperaSonnetDto modifySonnetsDto) {
        LOGGER.debug(modifySonnetsDto.toString());
        return corperaService.addSonnets(modifySonnetsDto.getCorperaId(), modifySonnetsDto.getSonnetId());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN, methods = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/corpera/remove_sonnet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeSonnetFromCorpera(@RequestBody @Valid CorperaSonnetDto modifySonnetsDto) {
        return corperaService.removeSonnets(modifySonnetsDto.getCorperaId(), modifySonnetsDto.getSonnetId());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN, methods = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/corpera/change_name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> ModifyCorperaDetails(@RequestBody @Valid CorperaModifyDto modifyDto) {
        return corperaService.modify(modifyDto.getCorperaId(), modifyDto.getName(), modifyDto.getDescription());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/corpera/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getAllCorperaSonnets(@PathVariable("id") String id) {
        return corperaService.getCorperaSonnets(id);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/corpera/get_paged/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Sonnet> getAllCorperaSonnetsPaged(@PathVariable("id") String id, Pageable pageable) {
        return corperaService.getCorperaSonnetsPaged(id, pageable);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN, methods = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping(value = "/secure/corpera/delete/{id}")
    public ResponseEntity<Void> deleteCorpus(@PathVariable("id") String id) {
        return corperaService.delete(id);
    }

}
