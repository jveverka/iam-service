package one.microproject.iamservice.server.config;

import one.microproject.iamservice.core.model.JWToken;
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

    private final ModelCache modelCache;

    public TokenCacheConfig(@Autowired ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Bean
    @Scope("singleton")
    public TokenCache getTokenCache(CacheHolder<JWToken> cache) {
        return new TokenCacheImpl(modelCache, cache);
    }

}
