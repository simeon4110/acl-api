package com.sonnets.sonnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.sonnets.sonnet.persistence.models",
        "com.sonnets.sonnet.persistence.repositories"
})
@EnableTransactionManagement
@Profile("test")
public class TestJpaConfig {
    @Bean
    @Profile("test")
    public DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl("jdbc:mysql://192.168.0.11:3306/sonnet_test_2?useSSL=true");
        driverManagerDataSource.setUsername("josh");
        driverManagerDataSource.setPassword("ToyCar11");
        return driverManagerDataSource;
    }
}
