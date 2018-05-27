package com.sonnets.sonnet.config;

import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
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
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeRequests()
            .antMatchers("/login", "/login/**", "/sonnet/login", "/sonnet/login/**").permitAll()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/").permitAll()
            .antMatchers("/lookup/", "/lookup/**").permitAll()
            .antMatchers("/browse/", "/browse/**").permitAll()
            .antMatchers("/sonnets/", "/sonnets/**").permitAll()
            .antMatchers("/edit/", "/edit/**").hasAuthority("USER")
            .antMatchers("/insert/", "/insert/**").hasAuthority("USER")
            .antMatchers("/admin", "/admin/add/", "/admin/**").hasAuthority("ADMIN")
            .antMatchers("/profile", "/profile/**").hasAuthority("USER")
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login").defaultSuccessUrl("/", true).permitAll()
            .and().logout().permitAll()
            .and().csrf().disable();
            // @formatter:on
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

}
