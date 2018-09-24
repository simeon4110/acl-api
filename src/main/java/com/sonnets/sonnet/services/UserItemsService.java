package com.sonnets.sonnet.services;

import com.sonnets.sonnet.services.prose.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Simple service for merging item types added by a user.
 *
 * @author Josh Harkema
 */
@Service
public class UserItemsService {
    private final SectionService sectionService;
    private final PoemService poemService;

    @Autowired
    public UserItemsService(SectionService sectionService, PoemService poemService) {
        this.sectionService = sectionService;
        this.poemService = poemService;
    }

    @Async
    @SuppressWarnings("unchecked")
    public CompletableFuture<List> getUserItems(Principal principal) {
        List results = new ArrayList();
        CompletableFuture<List> sections = sectionService.getUserSections(principal);
        CompletableFuture<List> poems = poemService.getAllByUser(principal);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(sections, poems);
        return allFutures.thenApply(future -> {
            results.addAll(sections.join());
            results.addAll(poems.join());
            return results;
        });
    }
}
