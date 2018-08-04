package com.sonnets.sonnet;

import com.sonnets.sonnet.persistence.models.Sonnet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author Josh Harkema
 */
@SpringBootApplication
@EntityScan(basePackageClasses = Sonnet.class)
public class SonnetApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SonnetApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SonnetApplication.class, args);
    }
}
