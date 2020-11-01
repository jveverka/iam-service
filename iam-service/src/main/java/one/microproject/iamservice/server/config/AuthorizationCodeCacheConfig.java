package one.microproject.iamservice.server.config;

import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix="iam-service.authorization-code-cache")
public class AuthorizationCodeCacheConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCodeCacheConfig.class);

    private long duration = 5;
    private String timeunit = "MINUTES";

    @PostConstruct
    public void init() {
        LOG.info("#CONFIG iam-service.authorization-code-cache.duration: {}", duration);
        LOG.info("#CONFIG iam-service.authorization-code-cache.timeunit: {}", timeunit);
    }

    @Bean
    @Scope("singleton")
    public AuthorizationCodeCache getCodeCache() {
        return new AuthorizationCodeCacheImpl(getDuration(), getTimeUnit());
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTimeunit() {
        return timeunit;
    }

    public void setTimeunit(String timeunit) {
        this.timeunit = timeunit;
    }

    public TimeUnit getTimeUnit() {
        return TimeUnit.valueOf(timeunit);
    }

}
