package com.sonnets.sonnet.configuration;

import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import generators.AuthorGenerator;
import generators.PoemGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;

/**
 * Configuration for the in-memory testing database.
 *
 * @author Josh Harkema
 */
@Configuration
@ActiveProfiles("test")
public class TestDataSourceConfig {
    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(false)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .build();
    }

    /**
     * This adds some testing data to the H2 test db. The user principle is a static override.
     *
     * @param authorRepository injected.
     * @param poemRepository   injected.
     * @return an empty lambda function result.
     */
    @Bean
    CommandLineRunner init(AuthorRepository authorRepository, PoemRepository poemRepository) {
        // This is hacky af, but it works.
        SecurityContextHolder.getContext().setAuthentication(new Authentication() {
            private static final long serialVersionUID = -3753530931120349739L;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singletonList(new SimpleGrantedAuthority("USER"));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return "user";
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "user";
            }
        });

        return args -> {
            Author author = authorRepository.saveAndFlush(AuthorGenerator.generate());
            int counter = 0;
            while (counter < 10) {
                Poem poem = PoemGenerator.generate(author);
                poemRepository.saveAndFlush(poem);
                counter++;
            }
        };
    }
}
