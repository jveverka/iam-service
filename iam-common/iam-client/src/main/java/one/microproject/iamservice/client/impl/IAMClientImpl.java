package one.microproject.iamservice.client.impl;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.client.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.client.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.client.JWTUtils.validateToken;

public class IAMClientImpl implements IAMClient {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientImpl.class);

    private final IAMServiceProxy iamServiceProxy;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public IAMClientImpl(IAMServiceProxy iamServiceProxy, OrganizationId organizationId, ProjectId projectId) {
        this.iamServiceProxy = iamServiceProxy;
        this.organizationId = organizationId;
        this.projectId = projectId;
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
            return JWTUtils.validateToken(organizationId, projectId, iamServiceProxy.getJWKResponse(), token);
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
            return JWTUtils.validateToken(organizationId, projectId, iamServiceProxy.getJWKResponse(), requiredAdminPermissions, requiredApplicationPermissions, token);
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
    public void close() throws Exception {
        iamServiceProxy.close();
    }

}
