package com.sonnets.sonnet.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.test.context.ActiveProfiles;

/**
 * Allows stateless security in the test environment.
 *
 * @author Josh Harkema
 */
@Configuration
@EnableResourceServer
@ActiveProfiles("test")
public class ResourceServerTestConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer security) throws Exception {
        security.stateless(false);
    }
}
