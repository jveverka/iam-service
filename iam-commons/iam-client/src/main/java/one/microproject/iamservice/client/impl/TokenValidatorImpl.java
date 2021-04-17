package one.microproject.iamservice.client.impl;

import one.microproject.iamservice.client.JWTUtils;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.KeyProvider;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;

import java.security.PublicKey;
import java.util.Optional;
import java.util.Set;

public class TokenValidatorImpl implements TokenValidator {

    @Override
    public Optional<StandardTokenClaims> validateToken(PublicKey key, JWToken token) {
        return JWTUtils.validateToken(key, token);
    }

    @Override
    public Optional<StandardTokenClaims> validateToken(KeyProvider keyProvider, JWToken token) {
        return JWTUtils.validateToken(keyProvider, token);
    }

    @Override
    public JWToken extractJwtToken(String authorization) {
        return JWTUtils.extractJwtToken(authorization);
    }

    @Override
    public Optional<StandardTokenClaims> validateToken(OrganizationId organizationId, ProjectId projectId, JWKResponse response, JWToken token) {
        return JWTUtils.validateToken(organizationId, projectId, response, token);
    }

    @Override
    public boolean validateToken(OrganizationId organizationId, ProjectId projectId, JWKResponse response, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions, JWToken token) {
        return JWTUtils.validateToken(organizationId, projectId, response, requiredAdminPermissions, requiredApplicationPermissions, token);
    }

}
