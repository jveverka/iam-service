package itx.iamservice.core.model;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Project {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final Map<ClientId, Client> clients;
    private final Map<RoleId, Role> roles;
    private final KeyPairData keyPairData;

    public Project(ProjectId id, String name, OrganizationId organizationId, PrivateKey organizationPrivateKey) throws PKIException {
        this.id = id;
        this.name = name;
        this.clients = new ConcurrentHashMap<>();
        this.organizationId = organizationId;
        this.roles = new ConcurrentHashMap<>();
        this.keyPairData = TokenUtils.createSignedPKIData(organizationId.getId(), id.getId(), 365L, TimeUnit.DAYS, organizationPrivateKey);
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

    public boolean remove(ClientId clientId) {
        return clients.remove(clientId) != null;
    }

    public Optional<Client> getClient(ClientId clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

    public void addRole(Role role) {
        roles.put(role.getId(), role);
    }

    public Optional<Role> getRole(RoleId id) {
        return Optional.ofNullable(roles.get(id));
    }

    public Collection<Role> getRoles() {
        return roles.values().stream().collect(Collectors.toList());
    }

    public boolean removeRole(RoleId id) {
        return roles.remove(id) != null;
    }

    public PrivateKey getPrivateKey() {
        return keyPairData.getPrivateKey();
    }

    public X509Certificate getCertificate() {
        return keyPairData.getX509Certificate();
    }

}
