package itx.iamservice.core.services.impl.caches;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
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
        ModelKey<Organization> organizationModelKey = ModelKey.from(Organization.class, organizationId);
        projects.keySet().forEach(k -> {
            if (k.startsWith(organizationModelKey)) {
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
        Project project = projects.get(projectKey(organizationId, projectId));
        if (project !=  null) {
            return project.getClient(clientId);
        }
        return Optional.empty();
    }

    private static ModelKey<Organization> organizationKey(OrganizationId id) {
        return ModelKey.from(Organization.class, id);
    }

    private static ModelKey<Project> projectKey(OrganizationId id, ProjectId projectId) {
        return ModelKey.from(Project.class, id, projectId);
    }

}
