package com.sonnets.sonnet.tools;


import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;

import java.util.UUID;

/**
 * Generates a sonnet with random data for testing.
 *
 * @author Josh Harkema
 */
public abstract class SonnetGenerator {
    private SonnetGenerator() {

    }

    public static SonnetDto sonnetGenerator() {
        SonnetDto sonnet = new SonnetDto();
        sonnet.setFirstName(UUID.randomUUID().toString());
        sonnet.setLastName(UUID.randomUUID().toString());
        sonnet.setTitle(UUID.randomUUID().toString());
        sonnet.setPeriod("1550-1600");
        sonnet.setPublicationYear(2222);
        sonnet.setPublicationStmt(UUID.randomUUID().toString());
        sonnet.setSourceDesc(UUID.randomUUID().toString());
        sonnet.setText(UUID.randomUUID().toString());

        return sonnet;
    }
}
