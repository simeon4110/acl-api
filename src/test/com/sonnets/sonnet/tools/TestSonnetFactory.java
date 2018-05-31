package com.sonnets.sonnet.tools;


import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;

import java.util.UUID;

/**
 * @author Josh Harkema
 */
public abstract class TestSonnetFactory {
    private TestSonnetFactory() {
        // Nuh-uh, you can't make me.
    }

    public static SonnetDto randomSonnetGenerator() {
        SonnetDto sonnetDto = new SonnetDto();
        sonnetDto.setFirstName(UUID.randomUUID().toString());
        sonnetDto.setLastName(UUID.randomUUID().toString());
        sonnetDto.setTitle(UUID.randomUUID().toString());
        sonnetDto.setPublicationYear(2222);
        sonnetDto.setPublicationStmt(UUID.randomUUID().toString());
        sonnetDto.setAddedBy("Admin");
        sonnetDto.setSourceDesc(UUID.randomUUID().toString());
        sonnetDto.setText(UUID.randomUUID().toString());

        return sonnetDto;
    }

    public static SonnetDto specificSonnetGenerator() {
        SonnetDto sonnetDto = new SonnetDto();
        sonnetDto.setFirstName("Bobby");
        sonnetDto.setLastName("Brown");
        sonnetDto.setTitle("Auto Generated Test Sonnet");
        sonnetDto.setPublicationYear(2222);
        sonnetDto.setPublicationStmt("TestSonnetFactory()");
        sonnetDto.setSourceDesc("Made literally a second ago.");
        sonnetDto.setText("This is the sonnet that doesn't end...\nYes it goes on and on MY FRIEND!");

        return sonnetDto;
    }
}
