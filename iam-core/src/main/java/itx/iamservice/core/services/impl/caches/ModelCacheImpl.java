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
import itx.iamservice.core.services.persistence.PersistenceService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public Model getModel() {
        return model;
    }

    @Override
    public void add(Organization organization) {
        ModelKey<Organization> key = organizationKey(organization.getId());
        organizations.put(key , organization);
        persistenceService.onNodeCreated(key, organization);
    }

    @Override
    public Collection<Organization> getOrganizations() {
        return organizations.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Organization> getOrganization(OrganizationId organizationId) {
        return Optional.ofNullable(organizations.get(organizationKey(organizationId)));
    }

    @Override
    public boolean remove(OrganizationId organizationId) {
        ModelKey<Organization> key = organizationKey(organizationId);
        Organization removed = organizations.remove(key);
        if (removed != null) {
            persistenceService.onNodeDeleted(key, removed);
        }
        return removed != null;
    }

    @Override
    public Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        Project project = projects.get(projectKey(organizationId, projectId));
        if (project !=  null) {
            return project.getUser(userId);
        }
        return Optional.empty();
    }

    @Override
    public void add(OrganizationId organizationId, ProjectId projectId, Client client) {
        Project project = projects.get(projectKey(organizationId, projectId));
        if (project !=  null) {
            project.addClient(client.getId());
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
        ModelKey<Project> key = projectKey(organizationId, project.getId());
        projects.put(key, project);
        persistenceService.onNodeCreated(key, project);
    }

    @Override
    public boolean remove(OrganizationId organizationId, ProjectId projectId) {
        ModelKey<Project> key = projectKey(organizationId, projectId);
        Project removed = projects.remove(key);
        if (removed != null) {
            persistenceService.onNodeDeleted(key, removed);
        }
        return removed != null;
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
        Project project = projects.get(projectKey(organizationId, projectId));
        if (project != null) {
            project.removeClient(clientId);
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
    public boolean add(OrganizationId organizationId, ProjectId projectId, Role role) {
        Project project = projects.get(projectKey(organizationId, projectId));
        if (project != null) {
            project.addRole(role.getId());
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
    public boolean remove(OrganizationId organizationId, ProjectId projectId, RoleId roleId) {
        Project project = projects.get(projectKey(organizationId, projectId));
        if (project != null) {
            project.removeRole(roleId);
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
        Project project = projects.get(projectKey(organizationId, projectId));
        Role role = roles.get(roleKey(organizationId, projectId, roleId));
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
    public boolean removePermissionFromRole(OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        Role role = roles.get(roleKey(organizationId, projectId, roleId));
        if (role != null) {
            return role.removePermission(permissionId);
        }
        return false;
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

}
