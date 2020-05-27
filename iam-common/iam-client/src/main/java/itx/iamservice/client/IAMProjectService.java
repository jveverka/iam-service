package itx.iamservice.client;

import itx.iamservice.core.model.KeyId;

import java.security.PrivateKey;
import java.util.Optional;

public interface IAMProjectService {

    Optional<PrivateKey> getKey(KeyId keyId);

}
