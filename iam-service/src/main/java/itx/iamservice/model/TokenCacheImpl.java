package itx.iamservice.model;

import itx.iamservice.services.dto.JWToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenCacheImpl implements TokenCache {

    private final Map<ClientId, JWToken> revokedJWTokens;

    public TokenCacheImpl() {
        this.revokedJWTokens = new ConcurrentHashMap<>();
    }

    @Override
    public void addRevokedToken(JWToken jwToken) {

    }

    @Override
    public void purgeRevokedTokens() {

    }

    @Override
    public boolean isRevoked(JWToken jwToken) {
        return false;
    }

}
