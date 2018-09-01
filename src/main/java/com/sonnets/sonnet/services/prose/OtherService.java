package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.prose.OtherDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.repositories.OtherRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Other.class related methods are here.
 *
 * @author Josh Harkema
 */
@Service
public class OtherService {
    private static final Logger LOGGER = Logger.getLogger(OtherService.class);
    private final GetObjectOrThrowNullException getObjectOrNull;
    private final OtherRepository otherRepository;

    @Autowired
    public OtherService(GetObjectOrThrowNullException getObjectOrNull, OtherRepository otherRepository) {
        this.getObjectOrNull = getObjectOrNull;
        this.otherRepository = otherRepository;
    }

    /**
     * Helper method for copying data from a dto to an Other object.
     *
     * @param other  the object to copy the data onto.
     * @param author the author of the Other object.
     * @param dto    with the new information.
     * @return Other object with the data copied.
     */
    private static Other createOrCopyOther(Other other, Author author, OtherDto dto) {
        other.setAuthor(author);
        other.setCategory(dto.getCategory());
        other.setTitle(dto.getTitle());
        other.setPublicationYear(dto.getPublicationYear());
        other.setPublicationStmt(dto.getPublicationStmt());
        other.setSourceDesc(dto.getSourceDesc());
        other.setPeriod(dto.getPeriod());
        other.setText(dto.getText());
        return other;
    }

    /**
     * Add a new Other object to the db.
     *
     * @param dto the dto with the new data.
     * @return OK if the object is added.
     */
    public ResponseEntity<Void> add(OtherDto dto) {
        LOGGER.debug("Adding other: " + dto.toString());
        Author author = getObjectOrNull.author(dto.getAuthorId());
        otherRepository.saveAndFlush(createOrCopyOther(new Other(), author, dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
