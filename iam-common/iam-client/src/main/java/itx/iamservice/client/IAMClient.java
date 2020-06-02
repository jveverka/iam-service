package itx.iamservice.client;

import com.nimbusds.jwt.JWTClaimsSet;
import itx.iamservice.core.model.JWToken;

import java.util.Optional;

public interface IAMClient extends AutoCloseable {

    Optional<JWTClaimsSet> validate(JWToken token);

}
