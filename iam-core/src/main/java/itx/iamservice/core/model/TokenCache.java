package itx.iamservice.core.model;

import itx.iamservice.core.services.dto.JWToken;

public interface TokenCache {

    void addRevokedToken(JWToken jwToken);

    int purgeRevokedTokens();

    boolean isRevoked(JWToken jwToken);

    int size();

}
