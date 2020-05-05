package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.keys.ModelKey;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModelImpl implements Model {

    private final ModelId id;
    private final String name;
    private final Map<ModelKey<Organization>, Organization> organizations;
    private final Map<ModelKey<Project>, Project> projects;
    private final Map<ModelKey<User>, User> users;
    private final Map<ModelKey<Client>, Client> clients;
    private final Map<ModelKey<Role>, Role> roles;

    public ModelImpl(ModelId id, String name) {
        this.id = id;
        this.name = name;
        this.organizations = new ConcurrentHashMap<>();
        this.projects = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.roles = new ConcurrentHashMap<>();
    }

    @JsonCreator
    public ModelImpl(@JsonProperty("id") ModelId id,
                     @JsonProperty("name") String name,
                     @JsonProperty("organizations") Collection<Organization> organizations) {
        this.id = id;
        this.name = name;
        this.organizations = new ConcurrentHashMap<>();
        this.projects = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.roles = new ConcurrentHashMap<>();
        organizations.forEach(organization -> this.organizations.put(organizationKey(organization.getId()), organization));
    }

    @Override
    public ModelId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void add(Organization organization) {
        organizations.put(organizationKey(organization.getId()), organization);
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
        return organizations.remove(organizationKey(organizationId)) != null;
    }

    @Override
    public Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        Organization organization = organizations.get(organizationKey(organizationId));
        if (organization != null) {
            Optional<Project> project = organization.getProject(projectId);
            if (project.isPresent()) {
                return project.get().getUser(userId);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId) {
        Organization organization = organizations.get(organizationKey(organizationId));
        if (organization != null) {
            return organization.getProject(projectId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        Organization organization = organizations.get(organizationKey(organizationId));
        if (organization != null) {
            Optional<Project> project = organization.getProject(projectId);
            if (project.isPresent()) {
                return project.get().getClient(clientId);
            }
        }
        return Optional.empty();
    }

    private static ModelKey<Organization> organizationKey(OrganizationId id) {
        return ModelKey.from(Organization.class, id);
    }

}
