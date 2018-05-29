package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.dtos.SonnetDto;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.TestSonnetFactory;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class SonnetIntegrationTests {
    private static final Logger logger = Logger.getLogger(SonnetIntegrationTests.class);
    @Autowired
    private SonnetDetailsService sonnetDetailsService;


    @Test
    public void addValidSonnetAndConfirm() {
        SonnetDto sonnetDto = TestSonnetFactory.specificSonnetGenerator();
        sonnetDetailsService.addNewSonnet(sonnetDto);
        Sonnet toCheck = sonnetDetailsService.getSonnetByTitleAndLastName(sonnetDto.getTitle(), sonnetDto.getLastName());

        Assert.assertEquals(toCheck.getFirstName(), sonnetDto.getFirstName());
        logger.debug("Assertion [] firstName is valid.");
        Assert.assertEquals(toCheck.getLastName(), sonnetDto.getLastName());
        logger.debug("Assertion [] lastName is valid.");
        Assert.assertEquals(toCheck.getTitle(), sonnetDto.getTitle());
        logger.debug("Assertion [] title is valid.");
        Assert.assertEquals(toCheck.getPublicationYear(), sonnetDto.getPublicationYear());
        logger.debug("Assertion [] publicationYear is valid.");
        Assert.assertEquals(toCheck.getPublicationStmt(), sonnetDto.getPublicationStmt());
        logger.debug("Assertion [] publicationStmt is valid.");
        Assert.assertEquals(toCheck.getSourceDesc(), sonnetDto.getSourceDesc());
        logger.debug("Assertion [] sourceDesc is valid.");
        Assert.assertEquals(toCheck.getAddedBy(), sonnetDto.getAddedBy());
        logger.debug("Assertion [] addedBy is valid.");
    }

    @Test
    public void addTwoIdenticalSonnets() {
        SonnetDto sonnetDto = TestSonnetFactory.randomSonnetGenerator();
        Sonnet sonnet = sonnetDetailsService.addNewSonnet(sonnetDto);
        Assert.assertEquals(sonnetDto.getTitle(), sonnet.getTitle());

        Sonnet sonnet1 = sonnetDetailsService.addNewSonnet(sonnetDto);
        Assert.assertEquals(null, sonnet1);
    }

}
