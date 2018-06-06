package com.sonnets.sonnet.tests;


import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.SonnetGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class SonnetIntegrationTests {
    @Autowired
    private SonnetDetailsService sonnetDetailsService;

    @Test
    public void addTwoIdenticalSonnets() {
        SonnetDto sonnetDto = SonnetGenerator.sonnetGenerator();
        Sonnet sonnet = sonnetDetailsService.addNewSonnet(sonnetDto);
        Assert.assertEquals(sonnetDto.getTitle(), sonnet.getTitle());

        Sonnet sonnet1 = sonnetDetailsService.addNewSonnet(sonnetDto);
        Assert.assertNull(sonnet1);
    }

}
