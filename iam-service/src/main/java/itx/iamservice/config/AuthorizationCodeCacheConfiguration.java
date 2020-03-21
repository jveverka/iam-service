package itx.iamservice.config;

import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.AuthorizationCodeCacheImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.TimeUnit;

@Configuration
public class AuthorizationCodeCacheConfiguration {

    @Bean
    @Scope("singleton")
    public AuthorizationCodeCache getCodeCache() {
        return new AuthorizationCodeCacheImpl(10L, TimeUnit.MINUTES);
    }

}
