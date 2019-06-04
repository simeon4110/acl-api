package org.acl.database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Josh Harkema
 */
@SpringBootApplication(scanBasePackages = {
        "org.acl.database"
})
@EntityScan(basePackages = {
        "org.acl.database.persistence.models.annotation",
        "org.acl.database.persistence.models.base",
        "org.acl.database.persistence.models.prose",
        "org.acl.database.persistence.models.tools",
        "org.acl.database.persistence.models.web"
})
@EnableScheduling
@EnableCaching
@EnableSwagger2
public class SonnetApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SonnetApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SonnetApplication.class, args);
    }
}
