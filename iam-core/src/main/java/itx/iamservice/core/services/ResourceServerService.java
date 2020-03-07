package itx.iamservice.core.services;

import itx.iamservice.core.services.dto.JWToken;

public interface ResourceServerService {

    boolean verify(JWToken token);

}
