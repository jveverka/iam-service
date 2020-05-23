package itx.iamservice.client.impl;

import itx.iamservice.client.IAMClient;
import itx.iamservice.core.model.JWTInfo;
import itx.iamservice.core.model.JWToken;

import java.util.Optional;

public class IAMClientImpl implements IAMClient {

    @Override
    public Optional<JWTInfo> validate(JWToken token) {
        return Optional.empty();
    }

}
