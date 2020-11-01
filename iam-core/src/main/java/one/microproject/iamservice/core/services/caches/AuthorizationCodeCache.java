package one.microproject.iamservice.core.services.caches;

import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.dto.Code;
import one.microproject.iamservice.core.services.dto.Scope;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

public interface AuthorizationCodeCache {

    AuthorizationCode issue(URI issuerUri, OrganizationId organizationId, ProjectId projectId, ClientId clientId, UserId userId, String state, Scope scope, Set<String> audience, String redirectURI);

    int purgeCodes();

    boolean setScope(Code code, Scope scope);

    Optional<AuthorizationCodeContext> verifyAndRemove(Code code);

    Optional<AuthorizationCodeContext> get(Code code);

}
