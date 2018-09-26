package com.sonnets.sonnet.config;

import com.sonnets.sonnet.security.AuditorAwareImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;

/**
 * Configuration for JPA auditing and transaction management.
 *
 * @author Josh Harkema
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "com.sonnets.sonnet.persistence.repositories",
        "com.sonnets.sonnet.persistence.repositories.book",
        "com.sonnets.sonnet.persistence.repositories.corpora",
        "com.sonnets.sonnet.persistence.repositories.items",
        "com.sonnets.sonnet.persistence.repositories.poem",
        "com.sonnets.sonnet.persistence.repositories.section"
})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableTransactionManagement
public class JpaConfig {
    private final EntityManager entityManager;

    @Autowired
    public JpaConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager.getEntityManagerFactory());

        return transactionManager;
    }

}
