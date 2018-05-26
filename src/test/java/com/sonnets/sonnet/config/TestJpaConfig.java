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
 * @author Josh Harkema
 */
@Configuration
@ComponentScan(basePackages = {"com.sonnets.sonnet"})
@EnableJpaRepositories(basePackages = {
        "com.sonnets.sonnet.models",
        "com.sonnets.sonnet.repositories"
})
@EnableTransactionManagement
public class TestJpaConfig {
    @Bean
    @Profile("test")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://192.168.0.11:3306/sonnet_test?useSSL=true");
        dataSource.setUsername("josh");
        dataSource.setPassword("ToyCar11");

        return dataSource;
    }
}
