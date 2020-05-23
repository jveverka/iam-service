package itx.iamservice.client;

import itx.iamservice.core.model.JWTInfo;
import itx.iamservice.core.model.JWToken;

import java.util.Optional;

public interface IAMClient {

    Optional<JWTInfo> validate(JWToken token);

}
