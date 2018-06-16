package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.exceptions.SonnetAlreadyExistsException;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class SonnetAlreadyExistsTest {
    private final static Logger logger = Logger.getLogger(SonnetAlreadyExistsTest.class);
    @Autowired
    private SonnetDetailsService sonnetDetailsService;


    @Test(expected = SonnetAlreadyExistsException.class)
    public void testAlreadyExists() {
        SonnetDto sonnetDto = new SonnetDto(sonnetDetailsService.getSonnetByID("875"));
        logger.debug("Testing sonnet: " + sonnetDto);

        sonnetDetailsService.addNewSonnet(sonnetDto);

    }
}
