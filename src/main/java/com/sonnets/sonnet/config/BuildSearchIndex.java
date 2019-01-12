package com.sonnets.sonnet.config;

import com.sonnets.sonnet.persistence.models.annotations.Dialog;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
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
@Component
@Transactional
public class BuildSearchIndex implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = Logger.getLogger(BuildSearchIndex.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onApplicationEvent(@SuppressWarnings("NullableProblems") final ApplicationReadyEvent event) {
        try {
            FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
            manager.createIndexer(Item.class, Author.class, Dialog.class, BookCharacter.class)
                    .startAndWait();
        } catch (InterruptedException e) {
            LOGGER.error(e);
            Thread.currentThread().interrupt();
        }
    }
}
