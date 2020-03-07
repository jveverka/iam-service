package itx.iamservice.core.services;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.services.dto.JWToken;

import java.util.Optional;

public interface ClientService {

    Optional<JWToken> authenticate(AuthenticationRequest authenticationRequest);

    Optional<JWToken> renew(JWToken token);

    boolean logout(JWToken token);

}
