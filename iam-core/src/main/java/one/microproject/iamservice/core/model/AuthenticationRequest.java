package one.microproject.iamservice.core.model;

import one.microproject.iamservice.core.services.dto.Scope;


/**
 * Authentication Request from user (subject).
 * @param <C> extends {@link Credentials}
 */
public interface AuthenticationRequest<C extends Credentials> {

    /**
     * Get unique ID of the user (subject).
     * @return {@link UserId}
     */
    UserId getUserId();

    /**
     * Get type of credentials used to verify this {@link Credentials}.
     * @return Credential type.
     */
    Class<C> getCredentialsType();

    /**
     * Scope requested by user (subject).
     * @return requested scope.
     */
    Scope getScope();

    /**
     * Get client's credentials.
     * @return client's credentials.
     */
    ClientCredentials getClientCredentials();

}
