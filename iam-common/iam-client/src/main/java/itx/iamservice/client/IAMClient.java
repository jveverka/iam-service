package itx.iamservice.client;

import com.nimbusds.jwt.JWTClaimsSet;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;

import java.util.Optional;
import java.util.Set;

public interface IAMClient extends AutoCloseable {

    Optional<JWTClaimsSet> validate(JWToken token);

    Optional<JWTClaimsSet> validate(OrganizationId organizationId, ProjectId projectId, JWToken token);

    boolean validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions, JWToken token);

    boolean validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredApplicationPermissions, JWToken token);

}
