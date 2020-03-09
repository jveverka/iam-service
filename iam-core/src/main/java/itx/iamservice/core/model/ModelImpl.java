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
    public void remove(OrganizationId organizationId) {
        organizations.remove(organizationId);
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
