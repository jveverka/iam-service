package itx.iamservice.core.model;

import java.util.Set;

/**
 * Authentication Request from client (subject).
 * @param <C>
 */
public interface AuthenticationRequest<C extends CredentialsType> {

    /**
     * Get unique ID of the client (subject).
     * @return {@link ClientId}
     */
    ClientId getClientId();

    /**
     * Get type of credentials used to verify this {@link Credentials}.
     * @return Credential type.
     */
    C getCredentialsType();

    /**
     * Scope requested by client (subject).
     * @return
     */
    Set<RoleId> getScope();

}
