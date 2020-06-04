package itx.iamservice.server.config;

import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.CacheCleanupScheduler;
import itx.iamservice.core.services.impl.caches.CacheCleanupSchedulerImpl;
import itx.iamservice.core.services.caches.TokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@ConfigurationProperties(prefix="iam-service.cache-cleanup-interval")
public class CacheSchedulerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(CacheSchedulerConfig.class);

    private final AuthorizationCodeCache authorizationCodeCache;
    private final TokenCache tokenCache;

    private long duration = 5;
    private String timeunit = "MINUTES";
    private CacheCleanupScheduler cacheCleanupScheduler;

    public CacheSchedulerConfig(@Autowired AuthorizationCodeCache authorizationCodeCache,
                                @Autowired TokenCache tokenCache) {
        this.authorizationCodeCache = authorizationCodeCache;
        this.tokenCache = tokenCache;
    }

    @PostConstruct
    public void init() {
        LOG.info("#CONFIG iam-service.cache-cleanup-interval.duration: {}", duration);
        LOG.info("#CONFIG iam-service.cache-cleanup-interval.timeunit: {}", timeunit);
        this.cacheCleanupScheduler = new CacheCleanupSchedulerImpl(getDuration(), getTimeUnit(), authorizationCodeCache, tokenCache);
        this.cacheCleanupScheduler.start();
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("#SCHEDULER: shutdown");
        try {
            this.cacheCleanupScheduler.close();
        } catch (Exception e) {
            LOG.error("Error shutting down CacheCleanupScheduler", e);
        }
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
