package com.sonnets.sonnet.config;

import com.sonnets.sonnet.persistence.models.Privilege;
import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.persistence.repositories.PrivilegeRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
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

import java.util.HashSet;
import java.util.Set;

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
    private final PrivilegeRepository privilegeRepository;
    private final UserRepository userRepository;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          PrivilegeRepository privilegeRepository, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.privilegeRepository = privilegeRepository;
        this.userRepository = userRepository;
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
            .antMatchers("/login").permitAll()
            .antMatchers("/").permitAll()
            .antMatchers("/lookup/", "/lookup/**").permitAll()
            .antMatchers("/browse/", "/browse/**").permitAll()
            .antMatchers("/sonnets/", "/sonnets/**").permitAll()
            .antMatchers("/edit/", "/edit/**").hasRole("USER")
            .antMatchers("/insert/").hasRole("USER")
            .antMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login").permitAll()
            .and().logout().permitAll()
            .and().csrf();
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

    /**
     * This is a tool method to add an initial admin user for testing.
     */
    private void addAdminUser() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(encoder().encode("ToyCar11"));

        Set<Privilege> privileges = new HashSet<>();
        privileges.add(privilegeRepository.findByName("USER"));
        privileges.add(privilegeRepository.findByName("ADMIN"));

        user.setPrivileges(privileges);
        userRepository.save(user);
    }
}
