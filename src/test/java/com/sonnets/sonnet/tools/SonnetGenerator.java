package com.sonnets.sonnet.tools;


import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;

import java.util.UUID;

/**
 * @author Josh Harkema
 */
public abstract class SonnetGenerator {
    private SonnetGenerator() {

    }

    public static SonnetDto sonnetGenerator() {
        SonnetDto sonnet = new SonnetDto();
        sonnet.setFirstName(UUID.randomUUID().toString());
        sonnet.setLastName(UUID.randomUUID().toString());
        sonnet.setPublicationYear(2222);
        sonnet.setPublicationStmt(UUID.randomUUID().toString());
        sonnet.setSourceDesc(UUID.randomUUID().toString());
        sonnet.setText(UUID.randomUUID().toString());

        return sonnet;
    }
}
