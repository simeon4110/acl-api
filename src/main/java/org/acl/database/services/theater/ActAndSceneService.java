package org.acl.database.services.theater;

import org.acl.database.persistence.dtos.theater.ActDto;
import org.acl.database.persistence.dtos.theater.SceneDto;
import org.acl.database.persistence.models.theater.Act;
import org.acl.database.persistence.models.theater.Play;
import org.acl.database.persistence.models.theater.Scene;
import org.acl.database.persistence.repositories.theater.ActRepository;
import org.acl.database.persistence.repositories.theater.PlayRepository;
import org.acl.database.persistence.repositories.theater.SceneRepository;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ActAndSceneService {
    private static final Logger LOGGER = Logger.getLogger(ActAndSceneService.class);
    private final ActRepository actRepository;
    private final SceneRepository sceneRepository;
    private final PlayRepository playRepository;

    @Autowired
    public ActAndSceneService(ActRepository actRepository, SceneRepository sceneRepository,
                              PlayRepository playRepository) {
        this.actRepository = actRepository;
        this.sceneRepository = sceneRepository;
        this.playRepository = playRepository;
    }

    public ResponseEntity<Void> addAct(ActDto dto) {
        LOGGER.debug("Adding new act: " + dto.toString());
        Play play = playRepository.findById(dto.getPlayId()).orElseThrow(ItemNotFoundException::new);
        Act act = new Act();
        act.setNumber(dto.getNumber());
        act.setNotes(dto.getNotes());
        act.setScenes(Collections.emptySet());
        act = actRepository.saveAndFlush(act);
        play.getActs().add(act);
        playRepository.saveAndFlush(play);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<Void> addScene(SceneDto dto) {
        LOGGER.debug("Adding new scene: " + dto.toString());
        Act act = actRepository.findById(dto.getActId()).orElseThrow(ItemNotFoundException::new);
        Scene scene = new Scene();
        scene.setNumber(dto.getNumber());
        scene.setNotes(dto.getNotes());
        scene.setLines(Collections.emptySet());
        scene.setDirections(Collections.emptySet());
        scene = sceneRepository.saveAndFlush(scene);
        act.getScenes().add(scene);
        actRepository.saveAndFlush(act);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
