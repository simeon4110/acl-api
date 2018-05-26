package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDto;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Josh Harkema
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
@SpringBootTest
public class SonnetIntegrationTests {
    @Autowired
    private SonnetDetailsService sonnetDetailsService;

    /**
     * Generates a test SonnetDto where all data == "test"
     *
     * @return the SonnetDto object.
     */
    private static SonnetDto generateTestSonnet() {
        SonnetDto sonnet = new SonnetDto();
        sonnet.setFirstName("test");
        sonnet.setLastName("test");
        sonnet.setTitle("test");
        sonnet.setPublicationYear("2018");
        sonnet.setPublicationStmt("test");
        sonnet.setSourceDesc("test");

        sonnet.setText("test");

        return sonnet;
    }

    /**
     * Test adding and recovering a sonnet from the database.
     */
    @Test
    public void addSonnet() {
        SonnetDto sonnet = generateTestSonnet();

        sonnetDetailsService.addNewSonnet(sonnet);

        Sonnet sonnet1 = sonnetDetailsService.getSonnetByTitleAndLastName("test", "test");

        Assert.assertEquals("test", sonnet1.getFirstName());
        Assert.assertEquals("test", sonnet1.getLastName());
        Assert.assertEquals("test", sonnet1.getTitle());
        Assert.assertEquals("test", sonnet1.getPublicationStmt());
        Assert.assertEquals("test", sonnet1.getPublicationStmt());
        Assert.assertEquals("test", sonnet1.getSourceDesc());
    }
}
