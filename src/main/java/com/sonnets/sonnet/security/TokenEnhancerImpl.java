package com.sonnets.sonnet.security;

import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class TokenEnhancerImpl implements TokenEnhancer {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        UserPrincipalImpl principal = (UserPrincipalImpl) oAuth2Authentication.getPrincipal();
        User user = userDetailsService.loadUserObjectByUsername(principal.getUsername());

        final Set<String> privileges = new HashSet<>();
        if (user.getPrivileges().contains(privilegeRepository.findByName("ADMIN"))) {
            String[] strings = new String[]{"ADMIN", "USER"};
            privileges.addAll(Arrays.asList(strings));
        } else if (user.getPrivileges().contains(privilegeRepository.findByName("USER"))) {
            String[] strings = new String[]{"USER"};
            privileges.addAll(Arrays.asList(strings));
        } else {
            String[] strings = new String[]{"GUEST"};
            privileges.addAll(Arrays.asList(strings));
        }

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setScope(privileges);

        return oAuth2AccessToken;
    }
}
