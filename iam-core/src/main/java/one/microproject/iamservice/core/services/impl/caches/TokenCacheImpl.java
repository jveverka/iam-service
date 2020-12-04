package one.microproject.iamservice.core.services.impl.caches;

import io.jsonwebtoken.impl.DefaultClaims;
import one.microproject.iamservice.client.JWTUtils;
import one.microproject.iamservice.client.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.utils.TokenUtils;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.model.JWToken;

import java.util.Optional;

public class TokenCacheImpl implements TokenCache {

    private final ModelCache modelCache;
    private CacheHolder<JWToken> revokedJWTokens;

    public TokenCacheImpl(ModelCache modelCache, CacheHolder<JWToken> cacheHolder) {
        this.revokedJWTokens = cacheHolder;
        this.modelCache = modelCache;
    }

    @Override
    public void addRevokedToken(JWToken jwToken) {
        this.revokedJWTokens.put(jwToken.getToken(), jwToken);
    }

    @Override
    public int purgeRevokedTokens() {
        int size = this.revokedJWTokens.size();
        this.revokedJWTokens.remove(this::isTokenInvalid);
        return size - this.revokedJWTokens.size();
    }

    @Override
    public boolean isRevoked(JWToken jwToken) {
        return this.revokedJWTokens.get(jwToken.getToken()) != null;
    }

    @Override
    public int size() {
        return this.revokedJWTokens.size();
    }

    private boolean isTokenInvalid(JWToken jwToken) {
        return !validateToken(jwToken);
    }

    private boolean validateToken(JWToken jwToken) {
        DefaultClaims defaultClaims = TokenUtils.extractClaims(jwToken);
        OrganizationId organizationId = OrganizationId.from(defaultClaims.getIssuer());
        ProjectId projectId = ProjectId.from(defaultClaims.getAudience());
        UserId userId = UserId.from(defaultClaims.getSubject());
        Optional<User> userOptional = this.modelCache.getUser(organizationId, projectId, userId);
        if (userOptional.isPresent()) {
            Optional<StandardTokenClaims> tokenClaims = JWTUtils.validateToken(userOptional.get().getCertificate().getPublicKey(), jwToken);
            return tokenClaims.isPresent();
        }
        return false;
    }

}
