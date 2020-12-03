package one.microproject.iamservice.core.services;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.dto.IdTokenRequest;
import one.microproject.iamservice.core.services.dto.Scope;

import java.net.URI;
import java.util.Set;

/**
 * Generate {@link TokenResponse} for different type of auth flows.
 */
public interface TokenGenerator {

    /**
     * Generate {@link TokenResponse} for Password Credentials flow or Refresh Token flow;
     * @param issuerUri
     * @param organizationId
     * @param project
     * @param user
     * @param userPermissions
     * @param scope
     * @param clientId
     * @param idTokenRequest
     * @return
     */
    TokenResponse generate(URI issuerUri, OrganizationId organizationId, Project project, User user, Set<Permission> userPermissions, Scope scope, ClientId clientId, IdTokenRequest idTokenRequest);

    /**
     * Generate {@link TokenResponse} for Client Credentials flow or Refresh Token flow;
     * @param issuerUri
     * @param organizationId
     * @param project
     * @param clientPermissions
     * @param client
     * @param scope
     * @param idTokenRequest
     * @return
     */
    TokenResponse generate(URI issuerUri, OrganizationId organizationId, Project project, Set<Permission> clientPermissions, Client client, Scope scope, IdTokenRequest idTokenRequest);

    /**
     * Generate {@link TokenResponse} for Authorization Code Grant flow;
     * @param context
     * @param user
     * @param idTokenRequest
     * @return
     */
    TokenResponse generate(AuthorizationCodeContext context, User user, IdTokenRequest idTokenRequest);

}
