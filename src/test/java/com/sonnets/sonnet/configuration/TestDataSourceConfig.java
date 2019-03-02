package com.sonnets.sonnet.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

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
}

