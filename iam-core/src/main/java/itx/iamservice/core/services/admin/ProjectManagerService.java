package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;

import java.util.Collection;
import java.util.Optional;

public interface ProjectManagerService {

    boolean create(OrganizationId id, ProjectId projectId, CreateProjectRequest createProjectRequest) throws PKIException;

    Optional<ProjectId> create(OrganizationId id, CreateProjectRequest createProjectRequest) throws PKIException;

    Collection<Project> getAll(OrganizationId id);

    Optional<Project> get(OrganizationId id, ProjectId projectId);

    boolean remove(OrganizationId id, ProjectId projectId);

    boolean addRole(OrganizationId id, ProjectId projectId, Role role);

    Optional<RoleId> addRole(OrganizationId id, ProjectId projectId, CreateRoleRequest createRoleRequest);

    boolean removeRole(OrganizationId id, ProjectId projectId, RoleId roleId);

    Collection<Role> getRoles(OrganizationId id, ProjectId projectId);

}
