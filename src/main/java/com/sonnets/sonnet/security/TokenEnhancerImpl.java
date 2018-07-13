package com.sonnets.sonnet.security;

import com.sonnets.sonnet.persistence.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenEnhancerImpl implements TokenEnhancer {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        UserPrincipalImpl principal = (UserPrincipalImpl) oAuth2Authentication.getPrincipal();
        User user = userDetailsService.loadUserObjectByUsername(principal.getUsername());
        final Map<String, Object> add = new HashMap<>();

        add.put("id", user.getId());
        add.put("username", user.getUsername());
        add.put("admin", user.isAdmin());

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(add);

        return oAuth2AccessToken;
    }
}
