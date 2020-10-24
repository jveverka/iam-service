package itx.iamservice.core.services;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.services.dto.ProviderConfigurationRequest;
import itx.iamservice.core.dto.ProviderConfigurationResponse;

import java.security.PublicKey;
import java.util.Optional;

public interface ProviderConfigurationService {

    /**
     * Get provider's configuration as specified:
     * https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig
     * https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata
     * https://tools.ietf.org/html/rfc8414#section-3
     * @param request - request for provider configuration containing {@link OrganizationId} and {@link ProjectId} and base URL of the server.
     * @return provider configuration as specified above.
     */
    ProviderConfigurationResponse getConfiguration(ProviderConfigurationRequest request);

    /**
     * Get JWK data for project
     * @param organizationId
     * @param projectId
     * @return
     */
    JWKResponse getJWKData(OrganizationId organizationId, ProjectId projectId);

    /**
     * Search for key by Key-ID.
     * @param organizationId
     * @param projectId
     * @param kid
     * @return
     */
    Optional<PublicKey> getKeyById(OrganizationId organizationId, ProjectId projectId, String kid);

    /**
     * Search for key by Key-ID.
     * @param organizationId
     * @param kid
     * @return
     */
    Optional<PublicKey> getKeyById(OrganizationId organizationId, String kid);

    /**
     * Search for key by Key-ID.
     * @param kid
     * @return
     */
    Optional<PublicKey> getKeyById(String kid);

}
