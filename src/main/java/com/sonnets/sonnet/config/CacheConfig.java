package com.sonnets.sonnet.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * For the redis cache.
 *
 * @author Josh Harkema
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Empty by design.
}
