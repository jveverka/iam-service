package itx.iamservice.core.model;

import java.util.Collection;
import java.util.stream.Collectors;

public class Project {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final ModelImpl model;

    public Project(ProjectId id, String name, OrganizationId organizationId, ModelImpl model) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.organizationId = organizationId;
    }

    public ProjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void add(Client client) {
        model.getClients().put(client.getId(), client);
    }

    public Collection<Client> getAllClients() {
        return model.getClients().values().stream()
                .filter(client -> client.getProjectId().equals(id))
                .collect(Collectors.toList());
    }

    public void remove(ClientId clientId) {
        model.getClients().remove(clientId);
    }

}
