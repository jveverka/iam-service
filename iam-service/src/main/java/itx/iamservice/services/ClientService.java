package itx.iamservice.services;

import itx.iamservice.model.AuthenticationRequest;
import itx.iamservice.services.dto.JWToken;

import java.util.Optional;

public interface ClientService {

    Optional<JWToken> authenticate(AuthenticationRequest authenticationRequest);

    Optional<JWToken> renew(JWToken token);

    boolean logout(JWToken token);

}
