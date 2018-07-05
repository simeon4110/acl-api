package com.sonnets.sonnet.config;

import com.sonnets.sonnet.security.AuthenticationProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration for Spring.
 *
 * @author Josh Harkema
 */
@Configuration
@EnableWebSecurity
@ComponentScan("com.sonnets.sonnet.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private AuthenticationProviderImpl authenticationProvider;
    private static final int ENCODER_STRENGTH = 11;

    public SecurityConfig() {
        super();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll();
        // @formatter:on
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(ENCODER_STRENGTH);
    }

    @Autowired
    public void setAuthenticationProvider(AuthenticationProviderImpl authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }
}
