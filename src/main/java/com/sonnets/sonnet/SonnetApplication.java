package com.sonnets.sonnet;

import com.sonnets.sonnet.models.Sonnet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Josh Harkema
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.sonnets.sonnet"})
@EnableAutoConfiguration
@EntityScan(basePackageClasses = Sonnet.class)
@EnableJpaRepositories("com.sonnets.sonnet.repositories")
public class SonnetApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SonnetApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SonnetApplication.class, args);
    }
}
