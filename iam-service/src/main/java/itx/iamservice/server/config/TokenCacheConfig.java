package itx.iamservice.server.config;

import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.services.impl.caches.TokenCacheImpl;
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
    public TokenCache getTokenCache() {
        return new TokenCacheImpl(modelCache);
    }

}
