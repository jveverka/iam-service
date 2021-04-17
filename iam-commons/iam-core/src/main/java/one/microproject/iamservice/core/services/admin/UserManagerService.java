package one.microproject.iamservice.core.services.admin;

import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.dto.CreateUserRequest;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserManagerService {

    Optional<User> create(OrganizationId id, ProjectId projectId, CreateUserRequest request) throws PKIException;

    Collection<User> getAll(OrganizationId id, ProjectId projectId);

    Optional<User> get(OrganizationId id, ProjectId projectId, UserId userId);

    boolean remove(OrganizationId id, ProjectId projectId, UserId userId);

    boolean assignRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId);

    boolean removeRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId);

    Set<RoleId> getRoles(OrganizationId id, ProjectId projectId, UserId userId);

    boolean setCredentials(OrganizationId id, ProjectId projectId, UserId userId, Credentials credentials);

}
