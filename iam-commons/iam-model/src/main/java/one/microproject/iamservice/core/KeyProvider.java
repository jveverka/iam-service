package one.microproject.iamservice.core;

import java.security.Key;

public interface KeyProvider {

    /**
     * Get {@link Key} by ID (kid)
     * @param keyId - unique key ID.
     * @return the {@link Key}.
     */
    Key getKey(String keyId);

}
