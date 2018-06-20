package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Verifies functionality of the random sonnet getter.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class RandomSonnetsTest {
    private static final Logger LOGGER = Logger.getLogger(RandomSonnetsTest.class);
    @Autowired
    private SonnetDetailsService sonnetDetailsService;

    @Test
    public void getRandomSonnets() {
        List<Sonnet> sonnets = sonnetDetailsService.getTwoRandomSonnets();

        Assert.assertEquals(3, sonnets.size());

        LOGGER.debug("Three random sonnets: " + sonnets.toString());
    }

}
