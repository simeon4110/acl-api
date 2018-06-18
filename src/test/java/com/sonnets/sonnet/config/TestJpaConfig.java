package com.sonnets.sonnet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Provides configuration for the sonnet_test database used in testing.
 *
 * @author Josh Harkema
 */
@Configuration
@ComponentScan(basePackages = {"com.sonnets.sonnet"})
@EnableJpaRepositories(basePackages = {
        "com.sonnets.sonnet.persistence.models",
        "com.sonnets.sonnet.persistence.repositories"
})
@EnableTransactionManagement
@Profile("test")
public class TestJpaConfig {
    private String SQL_HOST = "jdbc:mysql://192.168.0.11:3306/sonnet_test?useSSL=true";
    private String SQL_USER = "josh";
    private String SQL_PASS = "ToyCar11";

    /**
     * The test specific data source bean. Ensures all testing occurs on test database.
     *
     * @return a test specific DataSource.
     */
    @Bean
    @Profile("test")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(SQL_HOST);
        dataSource.setUsername(SQL_USER);
        dataSource.setPassword(SQL_PASS);

        return dataSource;
    }

}
