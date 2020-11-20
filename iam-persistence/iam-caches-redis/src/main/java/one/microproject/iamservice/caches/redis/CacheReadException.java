package one.microproject.iamservice.caches.redis;

public class CacheReadException extends RuntimeException {

    public CacheReadException(Throwable t) {
        super(t);
    }

}
