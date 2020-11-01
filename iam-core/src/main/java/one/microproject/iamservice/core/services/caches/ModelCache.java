package one.microproject.iamservice.core.services.caches;

import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;
import one.microproject.iamservice.core.services.dto.CreateProjectRequest;
import one.microproject.iamservice.core.services.dto.CreateUserRequest;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ModelCache {

    ModelWrapper export();

    Model getModel();

    //ORGANIZATION
    Optional<OrganizationId> add(Organization organization);

    Optional<Organization> getOrganization(OrganizationId organizationId);

    Collection<Organization> getOrganizations();

    boolean remove(OrganizationId organizationId);

    boolean removeWithDependencies(OrganizationId organizationId);

    void setProperty(OrganizationId id, String key, String value);

    void removeProperty(OrganizationId id, String key);

    //PROJECT
    Optional<Project> add(OrganizationId organizationId, CreateProjectRequest request) throws PKIException;

    Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId);

    Collection<Project> getProjects(OrganizationId organizationId);

    boolean remove(OrganizationId organizationId, ProjectId projectId);

    boolean removeWithDependencies(OrganizationId organizationId, ProjectId projectId);

    void setProperty(OrganizationId id, ProjectId projectId, String key, String value);

    void removeProperty(OrganizationId id, ProjectId projectId, String key);

    //USER
    Optional<User> add(OrganizationId organizationId, ProjectId projectId, CreateUserRequest request) throws PKIException;

    Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId);

    Collection<User> getUsers(OrganizationId organizationId, ProjectId projectId);

    Collection<User> getUsers(OrganizationId organizationId);

    boolean remove(OrganizationId organizationId, ProjectId projectId, UserId userId);

    boolean assignRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId);

    boolean removeRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId);

    boolean setCredentials(OrganizationId id, ProjectId projectId, UserId userId, Credentials credentials);

    //CLIENT
    Optional<Client> add(OrganizationId organizationId, ProjectId projectId, CreateClientRequest request);

    Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

    Collection<Client> getClients(OrganizationId organizationId, ProjectId projectId);

    boolean verifyClientCredentials(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials);

    boolean remove(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

    boolean assignRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

    boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

    //ROLE
    Optional<RoleId> add(OrganizationId organizationId, ProjectId projectId, Role role);

    Collection<Role> getRoles(OrganizationId organizationId, ProjectId projectId);

    Set<RoleId> getRoles(OrganizationId organizationId, ProjectId projectId, UserId userId);

    Optional<Role> getRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId);

    boolean remove(OrganizationId organizationId, ProjectId projectId, RoleId roleId);

    //PERMISSION - to - ROLE
    boolean addPermissionToRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId);

    boolean removePermissionFromRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId);

    //PERMISSIONS
    boolean addPermission(OrganizationId organizationId, ProjectId projectId, Permission permission);

    boolean removePermission(OrganizationId organizationId, ProjectId projectId, PermissionId permissionId);

    Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId);

    Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, UserId userId);

    Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

}
