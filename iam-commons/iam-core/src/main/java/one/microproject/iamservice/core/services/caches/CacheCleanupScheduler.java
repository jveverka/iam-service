package one.microproject.iamservice.core.services.caches;

/**
 * Background cache cleanup service.
 * This service runs on separate thread pool.
 */
public interface CacheCleanupScheduler extends AutoCloseable {

    /**
     * Start background cache cleanup scheduler.
     * This method is non-blocking.
     */
    void start();

}
