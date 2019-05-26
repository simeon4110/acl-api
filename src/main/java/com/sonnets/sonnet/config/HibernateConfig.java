package com.sonnets.sonnet.config;

import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

/**
 * Handles registration of concrete Hibernate integrator implementations with Hibernate.
 *
 * @author Josh Harkema
 */
@Configuration
public class HibernateConfig implements HibernatePropertiesCustomizer {
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(ReplicationEventListenerIntegrator.INSTANCE));
    }
}
