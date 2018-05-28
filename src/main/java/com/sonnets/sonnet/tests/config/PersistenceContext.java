package com.sonnets.sonnet.tests.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * @author Josh Harkema
 */
@Configuration
@EnableJpaAuditing
@EnableSpringDataWebSupport
public class PersistenceContext {
}
