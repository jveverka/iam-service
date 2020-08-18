package itx.iamservice.server.services.impl;

import itx.iamservice.client.impl.IAMServiceProxy;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.dto.ProviderConfigurationResponse;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.ProviderConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Deprecated
public class IAMServiceProxyImpl implements IAMServiceProxy {

    private static final Logger LOG = LoggerFactory.getLogger(IAMServiceProxyImpl.class);

    private final ProviderConfigurationService providerConfigurationService;

    public IAMServiceProxyImpl(@Autowired ProviderConfigurationService providerConfigurationService) {
        this.providerConfigurationService = providerConfigurationService;
    }

    @Override
    public JWKResponse getJWKResponse() {
        return providerConfigurationService.getJWKData(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
    }

    @Override
    public IntrospectResponse introspect(JWToken token, TokenType typeHint) {
        throw new UnsupportedOperationException("This method implementation is not required.");
    }

    @Override
    public ProviderConfigurationResponse getConfiguration() {
        throw new UnsupportedOperationException("This method implementation is not required.");
    }

    @Override
    public void close() throws Exception {
        LOG.info("close");
    }

}
