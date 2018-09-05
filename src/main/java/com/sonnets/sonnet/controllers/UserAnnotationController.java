package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.web.UserAnnotationDto;
import com.sonnets.sonnet.persistence.models.base.UserAnnotation;
import com.sonnets.sonnet.services.UserAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for user generated annotations. NullPointerExceptions are thrown for bad requests.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class UserAnnotationController {
    private final UserAnnotationService userAnnotationService;

    @Autowired
    public UserAnnotationController(UserAnnotationService userAnnotationService) {
        this.userAnnotationService = userAnnotationService;
    }

    /**
     * @param dto with annotation data.
     * @return OK if good.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/annotation/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid UserAnnotationDto dto) {
        return userAnnotationService.create(dto);
    }

    /**
     * @param dto with new annotation data.
     * @return OK if good.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/annotation/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid UserAnnotationDto dto) {
        return userAnnotationService.update(dto);

    }

    /**
     * @param id of the annotation to delete.
     * @return OK if good.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/annotation/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return userAnnotationService.destroy(id);
    }

    /**
     * @param id of the annotation to get.
     * @return the annotation if it exists, NullPointerException is thrown otherwise.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/annotation/get/{id}")
    public UserAnnotation get(@PathVariable("id") String id) {
        return userAnnotationService.get(id);
    }

    /**
     * @param userName the username to get annotations for.
     * @return a list of annotations. A NullPointerException is thrown if the user doesn't exist.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/annotation/get_by_user/{userName}")
    public List<UserAnnotation> getByUser(@PathVariable("userName") String userName) {
        return userAnnotationService.getAllByUser(userName);
    }
}
