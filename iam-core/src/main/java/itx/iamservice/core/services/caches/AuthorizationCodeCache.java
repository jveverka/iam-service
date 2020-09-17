package itx.iamservice.core.services.caches;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeContext;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.Scope;

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
