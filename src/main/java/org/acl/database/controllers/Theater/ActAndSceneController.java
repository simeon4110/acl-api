package org.acl.database.controllers.Theater;

import org.acl.database.persistence.dtos.theater.ActDto;
import org.acl.database.persistence.dtos.theater.SceneDto;
import org.acl.database.services.theater.ActAndSceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ActAndSceneController {
    private final ActAndSceneService actAndSceneService;

    @Autowired
    public ActAndSceneController(ActAndSceneService actAndSceneService) {
        this.actAndSceneService = actAndSceneService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/act", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addAct(@RequestBody @Valid ActDto dto) {
        return actAndSceneService.addAct(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/scene", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addScene(@RequestBody @Valid SceneDto dto) {
        return actAndSceneService.addScene(dto);
    }
}
