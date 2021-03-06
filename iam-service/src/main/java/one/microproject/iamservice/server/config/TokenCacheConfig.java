package one.microproject.iamservice.server.config;

import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.services.impl.caches.CacheHolder;
import one.microproject.iamservice.core.services.impl.caches.TokenCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TokenCacheConfig {

    @Bean
    @Scope("singleton")
    public TokenCache getTokenCache(@Autowired ModelCache modelCache,
                                    @Autowired CacheHolder<JWToken> cache,
                                    @Autowired TokenValidator tokenValidator) {
        return new TokenCacheImpl(modelCache, tokenValidator, cache);
    }

}
