package itx.iamservice.core.model;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModelImpl implements Model {

    private final Map<OrganizationId, Organization> organizations;

    public ModelImpl() {
        this.organizations = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Organization organization) {
        organizations.put(organization.getId(), organization);
    }

    @Override
    public Collection<Organization> getOrganizations() {
        return organizations.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Organization> getOrganization(OrganizationId organizationId) {
        return Optional.ofNullable(organizations.get(organizationId));
    }

    @Override
    public boolean remove(OrganizationId organizationId) {
        return organizations.remove(organizationId) != null;
    }

    @Override
    public Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        Organization organization = organizations.get(organizationId);
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
        Organization organization = organizations.get(organizationId);
        if (organization != null) {
            return organization.getProject(projectId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        Organization organization = organizations.get(organizationId);
        if (organization != null) {
            Optional<Project> project = organization.getProject(projectId);
            if (project.isPresent()) {
                return project.get().getClient(clientId);
            }
        }
        return Optional.empty();
    }

}
