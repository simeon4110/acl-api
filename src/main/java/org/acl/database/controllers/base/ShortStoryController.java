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
    @PostMapping(value = "/secure/short_story", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid ShortStoryDto dto) {
        return shortStoryService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/short_story/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return shortStoryService.delete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PutMapping(value = "/secure/short_story", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid ShortStoryDto dto, Principal principal) {
        return shortStoryService.modify(dto, principal);
    }

}
