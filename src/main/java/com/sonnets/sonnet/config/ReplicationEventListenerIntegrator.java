package com.sonnets.sonnet.config;

import com.sonnets.sonnet.persistence.listeners.ItemDeleteEventListener;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.stereotype.Component;

/**
 * Handles the registration of concrete Hibernate integrator implementations with Spring.
 *
 * @author Josh Harkema
 */
@Component
public class ReplicationEventListenerIntegrator implements Integrator {
    static final ReplicationEventListenerIntegrator INSTANCE = new ReplicationEventListenerIntegrator();

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactoryImplementor,
                          SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
        final EventListenerRegistry eventListenerRegistry =
                sessionFactoryServiceRegistry.getService(EventListenerRegistry.class);

        eventListenerRegistry.appendListeners(
                EventType.PRE_DELETE,
                ItemDeleteEventListener.INSTANCE
        );
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
        // Empty by design.
    }
}
