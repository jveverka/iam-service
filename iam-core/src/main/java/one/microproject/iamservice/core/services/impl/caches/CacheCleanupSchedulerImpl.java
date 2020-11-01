package one.microproject.iamservice.core.services.impl.caches;

import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.caches.CacheCleanupScheduler;
import one.microproject.iamservice.core.services.caches.TokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheCleanupSchedulerImpl implements CacheCleanupScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(CacheCleanupSchedulerImpl.class);

    private final ScheduledExecutorService executorService;
    private final long delay;
    private final TimeUnit timeUnit;

    private final AuthorizationCodeCache authorizationCodeCache;
    private final TokenCache tokenCache;

    public CacheCleanupSchedulerImpl(long delay, TimeUnit timeUnit,
                                     AuthorizationCodeCache authorizationCodeCache, TokenCache tokenCache) {
        this.executorService = Executors.newScheduledThreadPool(1);
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.authorizationCodeCache = authorizationCodeCache;
        this.tokenCache = tokenCache;
    }

    @Override
    public void start() {
        LOG.info("starting cache cleanup scheduler ...");
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                LOG.debug("cleaning authorization code and token caches ...");
                authorizationCodeCache.purgeCodes();
                tokenCache.purgeRevokedTokens();
            }
        }, 0L, delay, timeUnit);
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
    }

}
