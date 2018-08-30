package com.sonnets.sonnet.cron;

import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.TopicModel;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.services.PoemService;
import com.sonnets.sonnet.services.prose.SectionService;
import com.sonnets.sonnet.wordtools.MalletTools;
import com.sonnets.sonnet.wordtools.NLPTools;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.GenericAnnotationSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

/**
 * This handles all the pre-processing of documents. It runs a basic topic model and loads an NLP CoreDocument into
 * the database.
 *
 * @author Josh Harkema
 */
@Component
public class ScheduledTasks {
    private static final Logger LOGGER = Logger.getLogger(ScheduledTasks.class);
    private static final MalletTools malletTools = MalletTools.getInstance();
    private static final int POEM_TOPICS = 5;
    private static final int SECTION_TOPICS = 20;
    private final PoemService poemService;
    private final SectionService sectionService;

    @Autowired
    public ScheduledTasks(PoemService poemService, SectionService sectionService) {
        this.poemService = poemService;
        this.sectionService = sectionService;
    }

    // Creates a fake auth object for the
    private static void setAuth() {
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ADMIN", "USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "system", "ADMIN", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Async
    @Scheduled(cron = "0 0 2 * * * ")
    public void nlpDocumentLoader() {
        LOGGER.info("NLP document loading started.");
        setAuth();
        Poem poem = poemService.getUnprocessed();
        // Process poems.
        while (poem != null) {
            try {
                LOGGER.debug("Processing poem: " + poem.getId());

                // Load the poem's text as an NLP document, init serializer and OOS.
                CoreDocument document = NLPTools.loadDocument(String.valueOf(poem.getText()));
                GenericAnnotationSerializer serializer = new GenericAnnotationSerializer();
                ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream());

                // Add annotations.
                Annotation annotation = new Annotation();
                byte[] bytes = new byte[0];
                serializer.write(document.annotation(), out).write(bytes);
                annotation.setAnnotationBlob(bytes);
                poem.setAnnotation(annotation);

                // Add a topic model.
                TopicModel topicModel = new TopicModel();
                topicModel.setModel(malletTools.topicModel(poem.getTextPretty(), POEM_TOPICS));
                poem.setTopicModel(topicModel);
                poem.setProcessed(true);

                poemService.save(poem);
                poem = poemService.getUnprocessed();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        Section section = sectionService.getUnprocessed();
        while (section != null) {
            try {
                LOGGER.debug("Processing section: " + section.getId());

                // Load the section's text as an NLP document, init serializer and OOS.
                CoreDocument document = NLPTools.loadDocument(section.getText());
                GenericAnnotationSerializer serializer = new GenericAnnotationSerializer();
                ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream());

                // Add annotations.
                Annotation annotation = new Annotation();
                byte[] bytes = new byte[0];
                serializer.write(document.annotation(), out).write(bytes);
                annotation.setAnnotationBlob(bytes);
                section.setAnnotation(annotation);

                // Add a topic model.
                TopicModel topicModel = new TopicModel();
                topicModel.setModel(malletTools.topicModel(section.getText(), SECTION_TOPICS));
                section.setTopicModel(topicModel);
                section.setProcessed(true);

                sectionService.save(section);
                section = sectionService.getUnprocessed();

            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
