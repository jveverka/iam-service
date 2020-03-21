package itx.iamservice.config;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.services.caches.TokenCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TokenCacheConfiguration {

    private final Model model;

    public TokenCacheConfiguration(@Autowired Model model) {
        this.model = model;
    }

    @Bean
    @Scope("singleton")
    public TokenCache getTokenCache() {
        return new TokenCacheImpl(model);
    }

}
