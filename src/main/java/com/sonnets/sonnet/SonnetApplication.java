package com.sonnets.sonnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author Josh Harkema
 */
@SpringBootApplication
@EnableJpaAuditing
public class SonnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonnetApplication.class, args);
    }
}
