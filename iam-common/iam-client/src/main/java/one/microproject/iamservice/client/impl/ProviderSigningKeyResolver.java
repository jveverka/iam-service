package one.microproject.iamservice.client.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.UnsupportedJwtException;

import java.security.Key;

public class ProviderSigningKeyResolver extends SigningKeyResolverAdapter {

    private final KeyProvider provider;

    public ProviderSigningKeyResolver(KeyProvider provider) {
        this.provider = provider;
    }

    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        try {
            String keyId = jwsHeader.getKeyId();
            Key key = provider.getKey(keyId);
            if (key != null) {
                return key;
            } else {
                throw new UnsupportedJwtException("Key kid=" + keyId + " for JWT token not found !");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
