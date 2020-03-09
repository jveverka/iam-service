package itx.iamservice.core.model;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Project {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final Map<ClientId, Client> clients;

    public Project(ProjectId id, String name, OrganizationId organizationId) {
        this.id = id;
        this.name = name;
        this.clients = new ConcurrentHashMap<>();
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
        clients.put(client.getId(), client);
    }

    public Collection<Client> getAllClients() {
        return clients.values().stream()
                .filter(client -> client.getProjectId().equals(id))
                .collect(Collectors.toList());
    }

    public void remove(ClientId clientId) {
        clients.remove(clientId);
    }

    public Optional<Client> getClient(ClientId clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

}
