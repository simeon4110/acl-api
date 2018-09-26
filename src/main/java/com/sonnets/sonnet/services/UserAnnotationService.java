package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.web.UserAnnotationDto;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.base.UserAnnotation;
import com.sonnets.sonnet.persistence.repositories.UserAnnotationRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This handles all CRUD for user annotations.
 *
 * @author Josh Harkema
 */
@Service
public class UserAnnotationService {
    private static final Logger LOGGER = Logger.getLogger(UserAnnotationService.class);
    private final UserAnnotationRepository userAnnotationRepository;

    @Autowired
    public UserAnnotationService(UserAnnotationRepository userAnnotationRepository) {
        this.userAnnotationRepository = userAnnotationRepository;
    }

    private static UserAnnotation createOrCopyFromDto(UserAnnotation annotation, UserAnnotationDto dto) {
        annotation.setParentId(Long.parseLong(dto.getParentId()));
        annotation.setParentType(dto.getParentType());
        annotation.setText(dto.getText());
        annotation.setConfirmation(new Confirmation());
        return annotation;
    }

    public ResponseEntity<Void> create(UserAnnotationDto dto) {
        LOGGER.debug("Creating user annotation: " + dto.toString());
        UserAnnotation annotation = createOrCopyFromDto(new UserAnnotation(), dto);
        userAnnotationRepository.saveAndFlush(annotation);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> update(UserAnnotationDto dto) {
        LOGGER.debug("Updating user annotation: " + dto.toString());
        Optional<UserAnnotation> userAnnotation = userAnnotationRepository.findById(Long.parseLong(dto.getId()));
        userAnnotation.ifPresentOrElse(
                userAnnotation1 -> userAnnotationRepository.saveAndFlush(createOrCopyFromDto(userAnnotation1, dto)),
                ResponseEntity::notFound);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> destroy(String id) {
        LOGGER.debug("Deleteting user annotation: " + id);
        Optional<UserAnnotation> userAnnotation = userAnnotationRepository.findById(Long.parseLong(id));
        userAnnotation.ifPresentOrElse(userAnnotationRepository::delete, ResponseEntity::notFound);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public UserAnnotation get(String id) {
        LOGGER.debug("Getting user annotation: " + id);
        Optional<UserAnnotation> userAnnotation = userAnnotationRepository.findById(Long.parseLong(id));
        return userAnnotation.orElseThrow(NullPointerException::new);
    }

    public List<UserAnnotation> getAllByUser(String userName) {
        LOGGER.debug("Getting all of a user's user annotations: " + userName);
        Optional<List<UserAnnotation>> userAnnotations = userAnnotationRepository.findAllByCreatedBy(userName);
        return userAnnotations.orElseThrow(NullPointerException::new);
    }
}
