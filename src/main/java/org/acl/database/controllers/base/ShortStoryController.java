package org.acl.database.controllers.base;

import org.acl.database.persistence.dtos.base.ShortStoryDto;
import org.acl.database.persistence.dtos.base.ShortStoryOutDto;
import org.acl.database.persistence.models.base.ShortStory;
import org.acl.database.services.base.ShortStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
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
 * Controller for all short story related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class ShortStoryController implements AbstractItemController<ShortStory, ShortStoryDto, ShortStoryOutDto> {
    private final ShortStoryService shortStoryService;

    @Autowired
    public ShortStoryController(ShortStoryService shortStoryService) {
        this.shortStoryService = shortStoryService;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/short_story/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid ShortStoryDto dto) {
        return shortStoryService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/short_story/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return shortStoryService.delete(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/short_story/user_delete/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id, Principal principal) {
        return shortStoryService.userDelete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ShortStory getById(@PathVariable("id") Long id) {
        return shortStoryService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShortStory> getByIds(@PathVariable("ids") Long[] ids) {
        return shortStoryService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShortStoryOutDto> getAll() {
        return shortStoryService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/short_story/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShortStoryOutDto> authedUserGetAll() {
        return shortStoryService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ShortStory> getAllPaged(Pageable pageable) {
        return shortStoryService.getAllPaged(pageable);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/short_story/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShortStory> getAllByUser(Principal principal) {
        return shortStoryService.getAllByUser(principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/secure/short_story/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid ShortStoryDto dto) {
        return shortStoryService.modify(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/short_story/modify_user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyUser(@RequestBody @Valid ShortStoryDto dto, Principal principal) {
        return shortStoryService.modifyUser(dto, principal);
    }
}
