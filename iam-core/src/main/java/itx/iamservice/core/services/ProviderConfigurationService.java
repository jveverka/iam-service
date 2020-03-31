package itx.iamservice.core.services;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.dto.ProviderConfigurationRequest;
import itx.iamservice.core.services.dto.ProviderConfigurationResponse;

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

}
