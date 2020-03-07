package itx.iamservice.services;

import itx.iamservice.services.dto.JWToken;

public interface ResourceServerService {

    boolean verify(JWToken token);

}
