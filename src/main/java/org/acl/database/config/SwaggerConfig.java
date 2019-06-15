package org.acl.database.config;


import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

/**
 * Config for SpringFox automatic API documentation generator.
 *
 * @author Josh Harkema
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private final ServletContext servletContext;

    @Autowired
    public SwaggerConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Bean
    public Docket produceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathProvider(
                        // Override the servlet path.
                        new RelativePathProvider(servletContext) {
                            @Override
                            public String getApplicationBasePath() {
                                return "/";
                            }
                        })
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.acl.database.controllers"))
                .paths(paths())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("ACL public database API")
                .description("This page lists all the api endpoints for the ACL public literature database.")
                .version("3.3.0-SNAPSHOT")
                .build();
    }

    /**
     * Define the paths for SwaggerFox to generate auto docs for.
     *
     * @return a Guava predicate of paths.
     */
    @SuppressWarnings({"unchecked", "Guava"})
    private Predicate<String> paths() {
        return Predicates.and(
                PathSelectors.regex(
                        "/author.*|/book.*|/poem.*|/section.*|/short_story.*|/search.*|/basic_search.*|" +
                                "/secure/author.*|/secure/book.*|/secure/poem.*|/secure/section.*|" +
                                "/secure/short_story.*|/secure/stop_words.*|/secure/play.*|/play.*"
                ),
                Predicates.not(PathSelectors.regex("/error.*")),
                Predicates.not(PathSelectors.regex("/about.*"))
        );
    }
}
