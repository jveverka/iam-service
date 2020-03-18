package itx.iamservice.core.model;

import java.util.Set;

/**
 * Authentication Request from user (subject).
 * @param <C>
 */
public interface AuthenticationRequest<C extends CredentialsType> {

    /**
     * Get unique ID of the user (subject).
     * @return {@link UserId}
     */
    UserId getUserId();

    /**
     * Get type of credentials used to verify this {@link Credentials}.
     * @return Credential type.
     */
    C getCredentialsType();

    /**
     * Scope requested by user (subject).
     * @return
     */
    Set<RoleId> getScope();

}
