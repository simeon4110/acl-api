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
    public static final String SQL_SERVER_IP = "";
    public static final String SQL_DATABASE_NAME = "";
    public static final String SQL_PORT "";
    public static final String SQL_USER = "";
    public static final String SQL_PASS = "";
    
    /**
     * The test specific data source bean.
     *
     * @return a test specific DataSource.
     */
    @Bean
    @Profile("test")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://" + SQL_SERVER_IP + ":" + SQL_PORT + "/" + SQL_DATABASE_NAME + "?useSSL=true");
        dataSource.setUsername(SQL_USER);
        dataSource.setPassword(SQL_PASS);

        return dataSource;
    }

}
