package itx.iamservice.core.model;

import itx.iamservice.core.services.dto.Scope;


/**
 * Authentication Request from user (subject).
 * @param <C>
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
     * @return
     */
    Scope getScope();

    /**
     * Get client's credentials.
     * @return
     */
    ClientCredentials getClientCredentials();

}
