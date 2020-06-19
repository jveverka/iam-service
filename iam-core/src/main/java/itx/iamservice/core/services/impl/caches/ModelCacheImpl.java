package itx.iamservice.core.services.impl.caches;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
import itx.iamservice.core.services.persistence.PersistenceService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModelCacheImpl implements ModelCache {

    private final Model model;
    private final Map<ModelKey<Organization>, Organization> organizations;
    private final Map<ModelKey<Project>, Project> projects;
    private final Map<ModelKey<User>, User> users;
    private final Map<ModelKey<Client>, Client> clients;
    private final Map<ModelKey<Role>, Role> roles;

    private final PersistenceService persistenceService;

    public ModelCacheImpl(ModelWrapper modelWrapper, PersistenceService persistenceService) {
        this.model = modelWrapper.getModel();
        this.organizations = new ConcurrentHashMap<>();
        this.projects = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.roles = new ConcurrentHashMap<>();
        modelWrapper.getOrganizations().forEach(o -> this.organizations.put(o.getKey(), o.getValue()));
        modelWrapper.getProjects().forEach(o -> this.projects.put(o.getKey(), o.getValue()));
        modelWrapper.getUsers().forEach(o -> this.users.put(o.getKey(), o.getValue()));
        modelWrapper.getClients().forEach(o -> this.clients.put(o.getKey(), o.getValue()));
        modelWrapper.getRoles().forEach(o -> this.roles.put(o.getKey(), o.getValue()));
        this.persistenceService = persistenceService;
        this.persistenceService.onModelInitialization(modelWrapper);
    }

    public ModelCacheImpl(Model model, PersistenceService persistenceService) {
        this.model = model;
        this.organizations = new ConcurrentHashMap<>();
        this.projects = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.roles = new ConcurrentHashMap<>();
        this.persistenceService = persistenceService;
        this.persistenceService.onModelChange(model);
    }

    @Override
    public synchronized Model getModel() {
        return model;
    }

    /**
     * Organization methods
     **/

    @Override
    public synchronized Optional<OrganizationId> add(Organization organization) {
        ModelKey<Organization> key = organizationKey(organization.getId());
        if (organizations.get(key) == null) {
            organizations.put(key, organization);
            persistenceService.onNodeCreated(key, organization);
            return Optional.of(organization.getId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Collection<Organization> getOrganizations() {
        return organizations.values().stream().collect(Collectors.toList());
    }

    @Override
    public synchronized Optional<Organization> getOrganization(OrganizationId organizationId) {
        return Optional.ofNullable(organizations.get(organizationKey(organizationId)));
    }

    @Override
    public synchronized boolean remove(OrganizationId organizationId) {
        ModelKey<Organization> key = organizationKey(organizationId);
        Organization removed = organizations.remove(key);
        if (removed != null) {
            persistenceService.onNodeDeleted(key, removed);
        }
        return removed != null;
    }

    @Override
    public Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        User user = users.get(userKey(organizationId, projectId, userId));
        if (user !=  null) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getUsers(OrganizationId organizationId, ProjectId projectId) {
        List<User> result = new ArrayList<>();
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        users.keySet().forEach(k -> {
            if (k.startsWith(projectKey)) {
                result.add(users.get(k));
            }
        });
        return result;
    }

    @Override
    public boolean remove(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = projects.get(projectKey);
        ModelKey<User> key = userKey(organizationId, projectId, userId);
        User removed = users.remove(key);
        if (project != null) {
            project.remove(userId);
            persistenceService.onNodeUpdated(projectKey, project);
        }
        if (removed !=  null) {
            persistenceService.onNodeDeleted(key, removed);
        }
        return removed != null;
    }

    @Override
    public void add(OrganizationId organizationId, ProjectId projectId, Client client) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = projects.get(projectKey);
        if (project !=  null) {
            project.addClient(client.getId());
            persistenceService.onNodeUpdated(projectKey, project);
            ModelKey<Client> key = clientKey(organizationId, projectId, client.getId());
            clients.put(key, client);
            persistenceService.onNodeCreated(key, client);
        }
    }

    @Override
    public Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId) {
        Project project = projects.get(projectKey(organizationId, projectId));
        if (project != null) {
            return Optional.of(project);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Project> getProjects(OrganizationId organizationId) {
        List<Project> result = new ArrayList<>();
        ModelKey<Organization> organizationKey = ModelKey.from(Organization.class, organizationId);
        projects.keySet().forEach(k -> {
            if (k.startsWith(organizationKey)) {
                result.add(projects.get(k));
            }
        });
        return result;
    }

    @Override
    public void add(OrganizationId organizationId, Project project) {
        ModelKey<Organization> organizationKey = organizationKey(organizationId);
        Organization organization = organizations.get(organizationKey);
        if (organization != null) {
            organization.addProject(project.getId());
            persistenceService.onNodeUpdated(organizationKey, organization);
        }
        ModelKey<Project> key = projectKey(organizationId, project.getId());
        projects.put(key, project);
        persistenceService.onNodeCreated(key, project);
    }

    @Override
    public boolean remove(OrganizationId organizationId, ProjectId projectId) {
        ModelKey<Organization> organizationKey = organizationKey(organizationId);
        ModelKey<Project> key = projectKey(organizationId, projectId);
        Organization organization = organizations.get(organizationKey);
        if (organization != null) {
            organization.removeProject(projectId);
            persistenceService.onNodeUpdated(organizationKey, organization);
        }
        Project removed = projects.remove(key);
        if (removed != null) {
            persistenceService.onNodeDeleted(key, removed);
        }
        return removed != null;
    }

    @Override
    public void add(OrganizationId organizationId, ProjectId projectId, User user) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = projects.get(projectKey);
        if (project != null) {
            ModelKey<User> key = userKey(organizationId, projectId, user.getId());
            project.add(user.getId());
            persistenceService.onNodeUpdated(projectKey, project);
            users.put(key, user);
            persistenceService.onNodeCreated(key, user);
        }
    }

    @Override
    public Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        ModelKey<Client> key = clientKey(organizationId, projectId, clientId);
        Client client = clients.get(key);
        if (client !=  null) {
            return Optional.of(client);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Client> getClients(OrganizationId organizationId, ProjectId projectId) {
        List<Client> result = new ArrayList<>();
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        clients.keySet().forEach(k -> {
            if (k.startsWith(projectKey)) {
                result.add(clients.get(k));
            }
        });
        return result;
    }

    @Override
    public boolean verifyClientCredentials(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials) {
        ModelKey<Client> key = clientKey(organizationId, projectId, clientCredentials.getId());
        Client client = clients.get(key);
        if (client != null) {
            return clientCredentials.equals(client.getCredentials());
        }
        return false;
    }

    @Override
    public boolean remove(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = projects.get(projectKey);
        if (project != null) {
            project.removeClient(clientId);
            persistenceService.onNodeUpdated(projectKey, project);
            ModelKey<Client> key = clientKey(organizationId, projectId, clientId);
            Client removed = clients.remove(key);
            if (removed != null) {
                persistenceService.onNodeDeleted(key, removed);
            }
            return removed != null;
        }
        return false;
    }

    @Override
    public boolean assignRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        ModelKey<Client> clientKey = clientKey(id, projectId, clientId);
        Role role = roles.get(roleKey(id, projectId, roleId));
        Client client = clients.get(clientKey);
        if (role != null && client != null) {
            client.addRole(roleId);
            persistenceService.onNodeUpdated(clientKey, client);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        ModelKey<Client> clientKey = clientKey(id, projectId, clientId);
        Role role = roles.get(roleKey(id, projectId, roleId));
        Client client = clients.get(clientKey);
        if (role != null && client != null) {
            client.removeRole(roleId);
            persistenceService.onNodeUpdated(clientKey, client);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(OrganizationId organizationId, ProjectId projectId, Role role) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = projects.get(projectKey);
        if (project != null) {
            project.addRole(role.getId());
            persistenceService.onNodeUpdated(projectKey, project);
            ModelKey<Role> key = roleKey(organizationId, projectId, role.getId());
            roles.put(key, role);
            persistenceService.onNodeCreated(key, role);
            return true;
        }
        return false;
    }

    @Override
    public Collection<Role> getRoles(OrganizationId organizationId, ProjectId projectId) {
        List<Role> result = new ArrayList<>();
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        roles.keySet().forEach(k -> {
            if (k.startsWith(projectKey)) {
                result.add(roles.get(k));
            }
        });
        return result;
    }

    @Override
    public Optional<Role> getRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId) {
        ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
        return Optional.ofNullable(roles.get(roleKey));
    }

    @Override
    public boolean remove(OrganizationId organizationId, ProjectId projectId, RoleId roleId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = projects.get(projectKey);
        if (project != null) {
            project.removeRole(roleId);
            persistenceService.onNodeUpdated(projectKey, project);
            ModelKey<Role> key = roleKey(organizationId, projectId, roleId);
            Role removed = roles.remove(key);
            if (removed != null) {
                persistenceService.onNodeDeleted(key, removed);
            }
            return removed != null;
        }
        return false;
    }

    @Override
    public boolean addPermissionToRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Project project = projects.get(projectKey);
        ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
        Role role = roles.get(roleKey);
        if (role != null && project != null) {
            Optional<Permission> permission = project.getPermission(permissionId);
            if (permission.isPresent()) {
                role.addPermission(permission.get());
                persistenceService.onNodeUpdated(roleKey, role);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean assignRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        ModelKey<User> userKey = userKey(id, projectId, userId);
        Role role = roles.get(roleKey(id, projectId, roleId));
        User user = users.get(userKey);
        if (role != null && user != null) {
            user.addRole(roleId);
            persistenceService.onNodeUpdated(userKey, user);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        ModelKey<User> userKey = userKey(id, projectId, userId);
        Role role = roles.get(roleKey(id, projectId, roleId));
        User user = users.get(userKey);
        if (role != null && user != null) {
            user.removeRole(roleId);
            persistenceService.onNodeUpdated(userKey, user);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePermissionFromRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
        Role role = roles.get(roleKey);
        if (role != null) {
            boolean result = role.removePermission(permissionId);
            persistenceService.onNodeUpdated(roleKey, role);
            return result;
        }
        return false;
    }

    @Override
    public Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        return getPermissions(organizationId, projectId, userId, Set.of());
    }

    @Override
    public Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, UserId userId, Set<RoleId> roleFilter) {
        Set<Permission> result = new HashSet<>();
        ModelKey<User> userKey = userKey(organizationId, projectId, userId);
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        User user = users.get(userKey);
        Project  project =  projects.get(projectKey);
        if (user != null && project != null) {
            for (RoleId roleId: user.getRoles()) {
                if (roleFilter.isEmpty() || roleFilter.contains(roleId)) {
                    ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
                    Role role = roles.get(roleKey);
                    if (role != null) {
                        result.addAll(role.getPermissions());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        return getPermissions(organizationId, projectId, clientId, Set.of());
    }

    @Override
    public Set<Permission> getPermissions(OrganizationId organizationId, ProjectId projectId, ClientId clientId, Set<RoleId> roleFilter) {
        Set<Permission> result = new HashSet<>();
        ModelKey<Client> clientKey = clientKey(organizationId, projectId, clientId);
        ModelKey<Project> projectKey = projectKey(organizationId, projectId);
        Client client = clients.get(clientKey);
        Project  project =  projects.get(projectKey);
        if (client != null && project != null) {
            for (RoleId roleId: client.getRoles()) {
                if (roleFilter.isEmpty() || roleFilter.contains(roleId)) {
                    ModelKey<Role> roleKey = roleKey(organizationId, projectId, roleId);
                    Role role = roles.get(roleKey);
                    if (role != null) {
                        result.addAll(role.getPermissions());
                    }
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

}
