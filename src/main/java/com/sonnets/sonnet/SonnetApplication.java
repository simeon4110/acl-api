package com.sonnets.sonnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Josh Harkema
 */
@SpringBootApplication(scanBasePackages = {
        "com.sonnets.sonnet"
})
@EntityScan(basePackages = {
        "com.sonnets.sonnet.persistence.models.base",
        "com.sonnets.sonnet.persistence.models.poetry",
        "com.sonnets.sonnet.persistence.models.prose",
        "com.sonnets.sonnet.persistence.models.web",
        "com.sonnets.sonnet.persistence.models.annotation_types"
})
@EnableScheduling
public class SonnetApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SonnetApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SonnetApplication.class, args);
    }
}
