package itx.iamservice.model;

import itx.iamservice.services.dto.JWToken;

public interface TokenCache {

    void addRevokedToken(JWToken jwToken);

    void purgeRevokedTokens();

    boolean isRevoked(JWToken jwToken);

}
