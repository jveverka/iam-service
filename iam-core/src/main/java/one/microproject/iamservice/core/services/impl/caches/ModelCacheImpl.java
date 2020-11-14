package one.microproject.iamservice.core.services.impl.caches;

import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientImpl;
import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.ProjectImpl;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserImpl;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;
import one.microproject.iamservice.core.services.dto.CreateProjectRequest;
import one.microproject.iamservice.core.services.dto.CreateUserRequest;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class ModelCacheImpl implements ModelCache {

    private final ModelWrapper modelWrapper;

    public ModelCacheImpl(ModelWrapper modelWrapper) {
        this.modelWrapper = modelWrapper;
    }

    @Override
    public void onInit(PersistenceService persistenceService, boolean flushOnChange) throws Exception {
        modelWrapper.onInit(persistenceService, flushOnChange);
    }

    @Override
    public void flush() throws Exception {
        modelWrapper.flush();
    }

    @Override
    public synchronized Model getModel() {
        return this.modelWrapper.getModel();
    }

    /**
     * Organization methods
     **/

    @Override
    public synchronized Optional<OrganizationId> add(Organization organization) {
        ModelKey<Organization> key = organizationKey(organization.getId());
        if (modelWrapper.getOrganization(key) == null) {
            modelWrapper.putOrganization(key, organization);
            return Optional.of(organization.getId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Collection<Organization> getOrganizations() {
        return modelWrapper.getAllOrganizations();
    }

    @Override
    public synchronized Optional<Organization> getOrganization(OrganizationId organizationId) {
        return Optional.ofNullable(modelWrapper.getOrganization(organizationKey(organizationId)));
    }

    @Override
    public synchronized boolean remove(OrganizationId organizationId) {
        if (!checkOrganizationReferences(organizationId)) {
            ModelKey<Organization> key = organizationKey(organizationId);
            Organization removed = modelWrapper.removeOrganization(key);
            return removed != null;
        }
        return false;
    }

    @Override
    public boolean removeWithDependencies(OrganizationId organizationId) {
        ModelKey<Organization> organizationKey = organizationKey(organizationId);
        for (ModelKey<Project> key: modelWrapper.getProjectKeys()) {
            if (key.startsWith(organizationKey)) {
                removeWithDependencies(organizationId, ProjectId.from(key.getIds()[1].getId()));
            }
        }
        return remove(organizationId);
    }

    @Override
    public synchronized void setProperty(OrganizationId id, String key, String value) {
        ModelKey<Organization> organizationKey = organizationKey(id);
        Organization organization = modelWrapper.getOrganization(organizationKey);
        if (organization != null) {
            organization.setProperty(key, value);
        }
    }

    @Override
    public synchronized void removeProperty(OrganizationId id, String key) {
        ModelKey<Organization> organizationKey = organizationKey(id);
        Organization organization = modelWrapper.getOrganization(organizationKey);
        if (organization != null) {
            organization.removeProperty(key);
        }
    }

    /**
     * User Methods
     */

    @Override
    public synchronized Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        User user = modelWrapper.getUser(userKey(organizationId, projectId, userId));
        if (user !=  null) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public synchronized Collection<User> getUsers(OrganizationId organizationId, ProjectId projectId) {
        List<User> result = new ArrayList<>();
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        modelWrapper.getUserKeys().forEach(k -> {
            if (k.startsWith(projectKey)) {
                result.add(modelWrapper.getUser(k));
            }
        });
        return result;
    }

    @Override
    public synchronized Collection<User> getUsers(OrganizationId organizationId) {
        List<User> result = new ArrayList<>();
        ModelKey<Organization> organizationKey = organizationKey(organizationId);
        modelWrapper.getUserKeys().forEach(k -> {
            if (k.startsWith(organizationKey)) {
                result.add(modelWrapper.getUser(k));
            }
        });
        return result;
    }

    @Override
    public synchronized boolean remove(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = modelWrapper.getProject(projectKey);
        ModelKey<User> userKey = userKey(organizationId, projectId, userId);
        User removed = modelWrapper.removeUser(userKey);
        if (project != null) {
            project.remove(userId);
        }
        return removed != null;
    }

    @Override
    public synchronized Optional<Client> add(OrganizationId organizationId, ProjectId projectId, CreateClientRequest request) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        ModelKey<Client> clientKey = clientKey(organizationId, projectId, request.getId());
        Project project = modelWrapper.getProject(projectKey);
        Client c = modelWrapper.getClient(clientKey);
        if (project !=  null && c == null) {
            ClientCredentials credentials = new ClientCredentials(request.getId(), request.getSecret());
            Client client = new ClientImpl(credentials, request.getName(),
                    request.getDefaultAccessTokenDuration(), request.getDefaultRefreshTokenDuration(), request.getProperties());
            project.addClient(client.getId());
            modelWrapper.putClient(clientKey, client);
            return Optional.of(client);
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId) {
        Project project = modelWrapper.getProject(projectKey(organizationId, projectId));
        if (project != null) {
            return Optional.of(project);
        }
        return Optional.empty();
    }

    @Override
    public synchronized Collection<Project> getProjects(OrganizationId organizationId) {
        List<Project> result = new ArrayList<>();
        ModelKey<Organization> organizationKey = ModelKey.from(Organization.class, organizationId);
        modelWrapper.getProjectKeys().forEach(k -> {
            if (k.startsWith(organizationKey)) {
                result.add(modelWrapper.getProject(k));
            }
        });
        return result;
    }

    @Override
    public synchronized Optional<Project> add(OrganizationId organizationId, CreateProjectRequest request) throws PKIException {
        ModelKey<Organization> organizationKey = organizationKey(organizationId);
        ModelKey<Project> projectKey = projectKey(organizationId, request.getId());
        Organization organization = modelWrapper.getOrganization(organizationKey);
        Project p = modelWrapper.getProject(projectKey);
        if (organization != null && p == null) {
            organization.addProject(request.getId());
            ModelKey<Project> key = projectKey(organizationId, request.getId());
            Project project = new ProjectImpl(request.getId(),
                    request.getName(), organization.getId(), organization.getPrivateKey(), request.getAudience());
            modelWrapper.putProject(key, project);
            return Optional.of(project);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized boolean remove(OrganizationId organizationId, ProjectId projectId) {
        if (!checkProjectReferences(organizationId, projectId)) {
            ModelKey<Organization> organizationKey = organizationKey(organizationId);
            ModelKey<Project> key = projectKey(organizationId, projectId);
            Project removed = modelWrapper.removeProject(key);
            Organization organization = modelWrapper.getOrganization(organizationKey);
            if (organization != null) {
                organization.removeProject(projectId);
            }
            return removed != null;
        }
        return false;
    }

    @Override
    public synchronized boolean removeWithDependencies(OrganizationId organizationId, ProjectId projectId) {
        ModelKey<Project> projectKey = projectKey(organizationId,  projectId);
        for (ModelKey<User> key: modelWrapper.getUserKeys()) {
            if (key.startsWith(projectKey)) {
                modelWrapper.removeUser(key);
            }
        }
        for (ModelKey<Client> key: modelWrapper.getClientKeys()) {
            if (key.startsWith(projectKey)) {
                modelWrapper.removeClient(key);
            }
        }
        for (ModelKey<Role> key: modelWrapper.getRoleKeys()) {
            if (key.startsWith(projectKey)) {
                modelWrapper.removeRole(key);
            }
        }
        return remove(organizationId, projectId);
    }

    @Override
    public synchronized void setProperty(OrganizationId id, ProjectId projectId, String key, String value) {
        ModelKey<Project> projectKey = projectKey(id, projectId);
        Project project = modelWrapper.getProject(projectKey);
        if (project != null) {
            project.setProperty(key, value);
        }
    }

    @Override
    public synchronized void removeProperty(OrganizationId id, ProjectId projectId, String key) {
        ModelKey<Project> projectKey = projectKey(id, projectId);
        Project project = modelWrapper.getProject(projectKey);
        if (project != null) {
            project.removeProperty(key);
        }
    }

    @Override
    public synchronized Optional<User> add(OrganizationId organizationId, ProjectId projectId, CreateUserRequest request) throws PKIException {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        ModelKey<User> userKey = userKey(organizationId, projectId, request.getId());
        Project project = modelWrapper.getProject(projectKey);
        User u = modelWrapper.getUser(userKey);
        if (project != null && u == null) {
            User user = new UserImpl(request.getId(), request.getName(), project.getId(),
                    request.getDefaultAccessTokenDuration(), request.getDefaultRefreshTokenDuration(), project.getPrivateKey(), request.getEmail());
            ModelKey<User> key = userKey(organizationId, projectId, user.getId());
            project.add(user.getId());
            modelWrapper.putUser(key, user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        ModelKey<Client> key = clientKey(organizationId, projectId, clientId);
        Client client = modelWrapper.getClient(key);
        if (client !=  null) {
            return Optional.of(client);
        }
        return Optional.empty();
    }

    @Override
    public synchronized Collection<Client> getClients(OrganizationId organizationId, ProjectId projectId) {
        List<Client> result = new ArrayList<>();
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        modelWrapper.getClientKeys().forEach(k -> {
            if (k.startsWith(projectKey)) {
                result.add(modelWrapper.getClient(k));
            }
        });
        return result;
    }

    @Override
    public synchronized boolean verifyClientCredentials(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials) {
        ModelKey<Client> key = clientKey(organizationId, projectId, clientCredentials.getId());
        Client client = modelWrapper.getClient(key);
        if (client != null) {
            return clientCredentials.equals(client.getCredentials());
        }
        return false;
    }

    @Override
    public synchronized boolean remove(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = modelWrapper.getProject(projectKey);
        if (project != null) {
            project.removeClient(clientId);
            ModelKey<Client> clientKey = clientKey(organizationId, projectId, clientId);
            Client removed = modelWrapper.removeClient(clientKey);
            return removed != null;
        }
        return false;
    }

    @Override
    public synchronized boolean assignRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        ModelKey<Client> clientKey = clientKey(id, projectId, clientId);
        Role role = modelWrapper.getRole(roleKey(id, projectId, roleId));
        Client client = modelWrapper.getClient(clientKey);
        if (role != null && client != null) {
            client.addRole(roleId);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        ModelKey<Client> clientKey = clientKey(id, projectId, clientId);
        Role role = modelWrapper.getRole(roleKey(id, projectId, roleId));
        Client client = modelWrapper.getClient(clientKey);
        if (role != null && client != null) {
            client.removeRole(roleId);
            return true;
        }
        return false;
    }

    @Override
    public synchronized Optional<RoleId> add(OrganizationId organizationId, ProjectId projectId, Role role) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Role r = modelWrapper.getRole(roleKey(organizationId, projectId, role.getId()));
        Project project = modelWrapper.getProject(projectKey);
        if (project != null && r == null) {
            project.addRole(role.getId());
            ModelKey<Role> key = roleKey(organizationId, projectId, role.getId());
            modelWrapper.putRole(key, role);
            return Optional.of(role.getId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Collection<Role> getRoles(OrganizationId organizationId, ProjectId projectId) {
        List<Role> result = new ArrayList<>();
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        modelWrapper.getRoleKeys().forEach(k -> {
            if (k.startsWith(projectKey)) {
                result.add(modelWrapper.getRole(k));
            }
        });
        return result;
    }

    @Override
    public synchronized Set<RoleId> getRoles(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        ModelKey<User> userKey = userKey(organizationId, projectId, userId);
        User user = modelWrapper.getUser(userKey);
        if (user != null) {
            return Set.copyOf(user.getRoles());
        }
        return Set.of();
    }

    @Override
    public synchronized Optional<Role> getRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId) {
        ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
        return Optional.ofNullable(modelWrapper.getRole(roleKey));
    }

    @Override
    public synchronized boolean remove(OrganizationId organizationId, ProjectId projectId, RoleId roleId) {
        if (!checkRoleReferences(organizationId, projectId, roleId)) {
            ModelKey<Project> projectKey = projectKey(organizationId, projectId);
            Project project = modelWrapper.getProject(projectKey);
            if (project != null) {
                project.removeRole(roleId);
                ModelKey<Role> key = roleKey(organizationId, projectId, roleId);
                Role removed = modelWrapper.removeRole(key);
                return removed != null;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean addPermissionToRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = modelWrapper.getProject(projectKey);
        ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
        Role role = modelWrapper.getRole(roleKey);
        if (role != null && project != null) {
            Optional<Permission> permission = project.getPermission(permissionId);
            if (permission.isPresent()) {
                role.addPermission(permission.get());
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean assignRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        ModelKey<User> userKey = userKey(id, projectId, userId);
        Role role = modelWrapper.getRole(roleKey(id, projectId, roleId));
        User user = modelWrapper.getUser(userKey);
        if (role != null && user != null) {
            user.addRole(roleId);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        ModelKey<User> userKey = userKey(id, projectId, userId);
        Role role = modelWrapper.getRole(roleKey(id, projectId, roleId));
        User user = modelWrapper.getUser(userKey);
        if (role != null && user != null) {
            user.removeRole(roleId);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean setCredentials(OrganizationId id, ProjectId projectId, UserId userId, Credentials credentials) {
        ModelKey<User> userKey = userKey(id, projectId, userId);
        User user = modelWrapper.getUser(userKey);
        if (user != null) {
            user.addCredentials(credentials);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removePermissionFromRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
        Role role = modelWrapper.getRole(roleKey);
        if (role != null) {
            boolean result = role.removePermission(permissionId);
            return result;
        }
        return false;
    }

    @Override
    public synchronized boolean addPermission(OrganizationId organizationId, ProjectId projectId, Permission permission) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = modelWrapper.getProject(projectKey);
        if (project != null) {
            project.addPermission(permission);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removePermission(OrganizationId organizationId, ProjectId projectId, PermissionId permissionId) {
        if (!checkPermissionReferences(organizationId, projectId, permissionId)) {
            ModelKey<Project> projectKey = projectKey(organizationId, projectId);
            Project project = modelWrapper.getProject(projectKey);
            if (project != null) {
                project.removePermission(permissionId);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project =  modelWrapper.getProject(projectKey);
        if (project != null) {
            return Set.copyOf(project.getPermissions());
        }
        return Set.of();
    }

    @Override
    public synchronized Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        Set<Permission> result = new HashSet<>();
        ModelKey<User> userKey = userKey(organizationId, projectId, userId);
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        User user = modelWrapper.getUser(userKey);
        Project project =  modelWrapper.getProject(projectKey);
        if (user != null && project != null) {
            for (RoleId roleId: user.getRoles()) {
                 ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
                 Role role = modelWrapper.getRole(roleKey);
                 if (role != null) {
                     result.addAll(role.getPermissions());
                 }
            }
        }
        return result;
    }

    @Override
    public synchronized Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        Set<Permission> result = new HashSet<>();
        ModelKey<Client> clientKey = clientKey(organizationId, projectId, clientId);
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Client client = modelWrapper.getClient(clientKey);
        Project project =  modelWrapper.getProject(projectKey);
        if (client != null && project != null) {
            for (RoleId roleId: client.getRoles()) {
                 ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
                 Role role = modelWrapper.getRole(roleKey);
                 if (role != null) {
                     result.addAll(role.getPermissions());
                 }
            }
        }
        return result;
    }

    private static ModelKey<Organization> organizationKey(OrganizationId id) {
        return ModelKey.from(Organization.class, id);
    }

    private static ModelKey<Project> projectKey(OrganizationId id, ProjectId projectId) {
        return ModelKey.from(Project.class, id, projectId);
    }

    private static ModelKey<Client> clientKey(OrganizationId id, ProjectId projectId, ClientId clientId) {
        return ModelKey.from(Client.class, id, projectId, clientId);
    }

    private static ModelKey<Role> roleKey(OrganizationId id, ProjectId projectId, RoleId roleId) {
        return ModelKey.from(Role.class, id, projectId, roleId);
    }

    private static ModelKey<User> userKey(OrganizationId id, ProjectId projectId, UserId userId) {
        return ModelKey.from(User.class, id, projectId, userId);
    }

    private boolean checkOrganizationReferences(OrganizationId organizationId) {
        ModelKey<Organization> organizationKey = organizationKey(organizationId);
        for (ModelKey<Project> key: modelWrapper.getProjectKeys()) {
            if (key.startsWith(organizationKey)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkProjectReferences(OrganizationId organizationId, ProjectId projectId)  {
        ModelKey<Project> projectKey = projectKey(organizationId,  projectId);
        for (ModelKey<User> key: modelWrapper.getUserKeys()) {
            if (key.startsWith(projectKey)) {
                return true;
            }
        }
        for (ModelKey<Client> key: modelWrapper.getClientKeys()) {
            if (key.startsWith(projectKey)) {
                return true;
            }
        }
        for (ModelKey<Role> key: modelWrapper.getRoleKeys()) {
            if (key.startsWith(projectKey)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPermissionReferences(OrganizationId organizationId, ProjectId projectId, PermissionId permissionId) {
        ModelKey<Project> projectKey = projectKey(organizationId,  projectId);
        for (Map.Entry<ModelKey<Role>, Role> entry: modelWrapper.getRoleEntrySet()) {
            if (entry.getKey().startsWith(projectKey)) {
                for (Permission permission : entry.getValue().getPermissions()) {
                    if (permission.getId().equals(permissionId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkRoleReferences(OrganizationId organizationId, ProjectId projectId, RoleId roleId) {
        ModelKey<Project> projectKey = projectKey(organizationId,  projectId);
        for (Map.Entry<ModelKey<User>, User> entry: modelWrapper.getUserEntrySet()) {
            if (entry.getKey().startsWith(projectKey)) {
                return entry.getValue().getRoles().contains(roleId);
            }
        }
        for (Map.Entry<ModelKey<Client>, Client> entry: modelWrapper.getClientEntrySet()) {
            if (entry.getKey().startsWith(projectKey)) {
                return entry.getValue().getRoles().contains(roleId);
            }
        }
        return false;
    }

}
