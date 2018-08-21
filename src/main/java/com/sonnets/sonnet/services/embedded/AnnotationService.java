package com.sonnets.sonnet.services.embedded;

import com.sonnets.sonnet.persistence.dtos.base.AnnotationDto;
import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.AnnotationRepository;
import com.sonnets.sonnet.services.MessageService;
import com.sonnets.sonnet.services.helpers.GetObjectOrNull;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Deals with annotation related endpoints.
 *
 * @author Josh Harkema
 */
@Service
public class AnnotationService {
    private static final Logger LOGGER = Logger.getLogger(AnnotationService.class);
    private final AnnotationRepository annotationRepository;
    private final MessageService messageService;
    private final GetObjectOrNull getObjectOrNull;

    @Autowired
    public AnnotationService(AnnotationRepository annotationRepository, MessageService messageService,
                             GetObjectOrNull getObjectOrNull) {
        this.annotationRepository = annotationRepository;
        this.messageService = messageService;
        this.getObjectOrNull = getObjectOrNull;
    }

    private static Annotation copyParentDetails(Annotation annotation, AnnotationDto dto) {
        annotation.setDescription(dto.getDescription());
        annotation.setAnnotationBlob(null);
        return annotation;
    }

    public ResponseEntity<Void> modifySection(AnnotationDto dto) {
        LOGGER.debug("Modifying annotation for section: " + dto.toString());
        Section section = getObjectOrNull.section(dto.getParentId());
        Annotation annotation = getObjectOrNull.annotation(dto.getAnnotationId());
        if (section != null && annotation != null) {
            annotationRepository.saveAndFlush(copyParentDetails(annotation, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> modifyOther(AnnotationDto dto) {
        LOGGER.debug("Modifying annotation for other: " + dto.toString());
        Other other = getObjectOrNull.other(dto.getParentId());
        Annotation annotation = getObjectOrNull.annotation(dto.getAnnotationId());
        if (other != null && annotation != null) {
            annotationRepository.saveAndFlush(copyParentDetails(annotation, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> modifyPoem(AnnotationDto dto) {
        LOGGER.debug("Modifying annotation for poem: " + dto.toString());
        Poem poem = getObjectOrNull.poem(dto.getParentId());
        Annotation annotation = getObjectOrNull.annotation(dto.getAnnotationId());
        if (poem != null && annotation != null) {
            annotationRepository.saveAndFlush(copyParentDetails(annotation, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
