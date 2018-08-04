package com.sonnets.sonnet.config;

import com.sonnets.sonnet.persistence.models.User;
import com.sonnets.sonnet.security.UserPrincipalImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Concrete class for JPA auditing via AuditorAware.
 *
 * @author Josh Harkema
 */
public class AuditorAwareImpl implements AuditorAware<User> {
    @Override
    public Optional<User> getCurrentAuditor() {
        UserPrincipalImpl principal = (UserPrincipalImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.ofNullable(principal.getUser());
    }
}
