package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tests.config.TestJpaConfig;
import com.sonnets.sonnet.tests.tools.SonnetGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
public class ThreadSafeTests {
    private static final Logger logger = Logger.getLogger(ThreadSafeTests.class);
    @Autowired
    private SonnetDetailsService sonnetDetailsService;

    /**
     * Simple helper class to create thread queues.
     *
     * @param runnable    a runnable or lambda.
     * @param threadCount the amount of threads to run.
     * @throws InterruptedException if the thread is interrupted.
     */
    private static void runMultithreaded(Runnable runnable, int threadCount) throws InterruptedException {
        List<Thread> threadList = new LinkedList<Thread>();
        for (int i = 0; i < threadCount; i++) {
            threadList.add(new Thread(runnable));
        }
        for (Thread t : threadList) {
            t.start();
        }
        for (Thread t : threadList) {
            t.join();
        }
    }

    @Test
    public void testMultithreadedGet() throws InterruptedException {
        runMultithreaded(() -> {
            try {
                logger.debug(sonnetDetailsService.getAllSonnets().toString());
            } catch (Exception e) {
                logger.error(e);
            }
        }, 10);
    }

    @Test
    public void testMultithreadedPut() throws InterruptedException {
        runMultithreaded(() -> {
            try {
                logger.debug("Adding sonnet...");
                sonnetDetailsService.addNewSonnet(SonnetGenerator.sonnetGenerator());
            } catch (Exception e) {
                logger.error(e);
            }
        }, 25);
    }

}