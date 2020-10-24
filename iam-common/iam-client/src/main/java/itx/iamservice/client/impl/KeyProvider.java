package itx.iamservice.client.impl;

import java.security.Key;

public interface KeyProvider {

    Key getKey(String keyId);

}
