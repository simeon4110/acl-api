package com.sonnets.sonnet.config;

import com.sonnets.sonnet.models.Sonnet;
import org.apache.log4j.Logger;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This rebuilds the lucene index each time the app is restarted. For searching.
 *
 * @author Josh Harkema
 */
@Transactional
@Component
public class BuildSearchIndex implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = Logger.getLogger(BuildSearchIndex.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        try {
            FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
            manager.createIndexer(Sonnet.class).startAndWait();
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }
}
