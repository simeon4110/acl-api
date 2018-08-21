package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.prose.OtherDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.prose.Other;
import com.sonnets.sonnet.persistence.repositories.OtherRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrNull;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OtherService {
    private static final Logger LOGGER = Logger.getLogger(OtherService.class);
    private final GetObjectOrNull getObjectOrNull;
    private final OtherRepository otherRepository;

    @Autowired
    public OtherService(GetObjectOrNull getObjectOrNull, OtherRepository otherRepository) {
        this.getObjectOrNull = getObjectOrNull;
        this.otherRepository = otherRepository;
    }

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

    public ResponseEntity<Void> add(OtherDto dto) {
        LOGGER.debug("Adding other: " + dto.toString());
        Author author = getObjectOrNull.author(dto.getAuthorId());
        if (otherRepository.findByAuthor_LastNameAndTitle(author.getLastName(), dto.getTitle()) == null) {
            otherRepository.saveAndFlush(createOrCopyOther(new Other(), author, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
