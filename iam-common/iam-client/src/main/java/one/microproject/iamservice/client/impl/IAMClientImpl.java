package one.microproject.iamservice.client.impl;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class IAMClientImpl implements IAMClient {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientImpl.class);

    private final TokenValidator tokenValidator;
    private final IAMServiceProxy iamServiceProxy;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public IAMClientImpl(TokenValidator tokenValidator, IAMServiceProxy iamServiceProxy, OrganizationId organizationId, ProjectId projectId) {
        this.iamServiceProxy = iamServiceProxy;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.tokenValidator = tokenValidator;
    }

    @Override
    public boolean waitForInit(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return iamServiceProxy.waitForInit(timeout, timeUnit);
    }

    @Override
    public Optional<StandardTokenClaims> validate(JWToken token) {
        return validate(organizationId, projectId, token);
    }

    @Override
    public Optional<StandardTokenClaims> validate(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        try {
            return tokenValidator.validateToken(organizationId, projectId, iamServiceProxy.getJWKResponse(), token);
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        LOG.debug("token validation has failed.");
        return Optional.empty();
    }

    @Override
    public boolean validate(OrganizationId organizationId, ProjectId projectId,
                            Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions,
                            JWToken token) {
        try {
            return tokenValidator.validateToken(organizationId, projectId, iamServiceProxy.getJWKResponse(), requiredAdminPermissions, requiredApplicationPermissions, token);
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return false;
    }

    @Override
    public boolean validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredApplicationPermissions, JWToken token) {
        return validate(organizationId, projectId, Set.of(), requiredApplicationPermissions, token);
    }

    @Override
    public void updateKeyCache() {
        iamServiceProxy.updateKeyCache();
    }

    @Override
    public TokenResponseWrapper getAccessTokensOAuth2AuthorizationCodeGrant(Code code, String state) throws IOException {
        return iamServiceProxy.getTokens(code, state);
    }

    @Override
    public TokenResponseWrapper getAccessTokensOAuth2AuthorizationCodeGrant(Code code, String state, String codeVerifier) throws IOException {
        return iamServiceProxy.getTokens(code, state, codeVerifier);
    }

    @Override
    public void close() throws Exception {
        iamServiceProxy.close();
    }

}
