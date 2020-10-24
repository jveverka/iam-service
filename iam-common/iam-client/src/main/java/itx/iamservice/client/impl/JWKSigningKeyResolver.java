package itx.iamservice.client.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.UnsupportedJwtException;
import itx.iamservice.core.dto.JWKData;
import itx.iamservice.core.dto.JWKResponse;

import java.security.Key;
import java.util.Optional;

import static itx.iamservice.client.JWTUtils.createPublicKey;

public class JWKSigningKeyResolver extends SigningKeyResolverAdapter {

    private final JWKResponse response;

    public JWKSigningKeyResolver(JWKResponse response) {
        this.response = response;
    }

    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        try {
            String keyId = jwsHeader.getKeyId();
            Optional<JWKData> jkwDataOptional = response.getKeys().stream().filter(jwkData -> keyId.equals(jwkData.getKeyId())).findFirst();
            if (jkwDataOptional.isPresent()) {
                return createPublicKey(jkwDataOptional.get());
            } else {
                throw new UnsupportedJwtException("Key kid=" + keyId + " for JWT token not found !");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
