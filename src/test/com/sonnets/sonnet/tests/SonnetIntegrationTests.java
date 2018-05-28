package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.persistence.dtos.SonnetDto;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tests.config.TestJpaConfig;
import com.sonnets.sonnet.tests.tools.SonnetGenerator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
public class SonnetIntegrationTests {
    private static final Logger logger = Logger.getLogger(SonnetIntegrationTests.class);
    @Autowired
    private SonnetDetailsService sonnetDetailsService;

    @Test
    public void addTwoIdenticalSonnets() {
        SonnetDto sonnetDto = SonnetGenerator.sonnetGenerator();
        Sonnet sonnet = sonnetDetailsService.addNewSonnet(sonnetDto);
        Assert.assertEquals(sonnetDto.getTitle(), sonnet.getTitle());

        Sonnet sonnet1 = sonnetDetailsService.addNewSonnet(sonnetDto);
        Assert.assertEquals(sonnet1, null);
    }
}
