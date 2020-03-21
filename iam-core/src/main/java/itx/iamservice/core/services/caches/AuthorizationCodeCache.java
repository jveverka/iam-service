package itx.iamservice.core.services.caches;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeContext;

import java.util.Optional;
import java.util.Set;

public interface AuthorizationCodeCache {

    AuthorizationCode issue(OrganizationId organizationId, ProjectId projectId, UserId userId, String state, Set<RoleId> scope);

    int purgeCodes();

    Optional<AuthorizationCodeContext> verifyAndRemove(String code);

}
