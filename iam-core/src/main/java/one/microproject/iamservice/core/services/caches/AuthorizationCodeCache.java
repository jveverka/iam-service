package one.microproject.iamservice.core.services.caches;

import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.services.dto.Scope;

import java.util.Optional;

public interface AuthorizationCodeCache {

    AuthorizationCode save(Code code, AuthorizationCodeContext authorizationCodeContext);

    int purgeCodes();

    boolean setScope(Code code, Scope scope);

    Optional<AuthorizationCodeContext> verifyAndRemove(Code code);

    Optional<AuthorizationCodeContext> get(Code code);

}
