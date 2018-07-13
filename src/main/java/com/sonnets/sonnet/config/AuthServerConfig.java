package com.sonnets.sonnet.config;

import com.sonnets.sonnet.security.TokenEnhancerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;

/**
 * OAuth2 authorization server config. WIP.
 *
 * @author Josh Harkema
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    private DataSource dataSourceApi;
    @Value("classpath:schema.sql")
    private Resource schemaScript;
    private PasswordEncoder encoder;
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    public AuthServerConfig(AuthenticationManager authenticationManager, DataSource dataSourceApi, PasswordEncoder encoder,
                            CorsConfigurationSource corsConfigurationSource) {
        this.authenticationManager = authenticationManager;
        this.dataSourceApi = dataSourceApi;
        this.encoder = encoder;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    public AuthServerConfig() {
        super();
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {

        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients()
                .addTokenEndpointAuthenticationFilter(new CorsFilter(corsConfigurationSource));

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        String secret = encoder.encode("");
        clients.jdbc(dataSourceApi)
                .withClient("databaseFrontEnd")
                .authorizedGrantTypes("implicit")
                .secret(secret)
                .scopes("read")
                .autoApprove(true)
                .and()
                .withClient("databaseAuthentication")
                .secret(secret)
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("read")
                .accessTokenValiditySeconds(1209600); // Set expiry to two weeks.
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .tokenEnhancer(tokenEnhancer())
                .tokenStore(tokenStore()).exceptionTranslator(webResponseExceptionTranslator())
                .authenticationManager(authenticationManager);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSourceApi);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new TokenEnhancerImpl();
    }

    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        return populator;
    }

    @Bean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
                OAuth2Exception body = responseEntity.getBody();
                HttpHeaders headers = new HttpHeaders();
                headers.setAll(responseEntity.getHeaders().toSingleValueMap());
                return new ResponseEntity<>(body, headers, responseEntity.getStatusCode());
            }
        };
    }

}
