package itx.iamservice.core.model;

import itx.iamservice.core.model.utils.TokenUtils;

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
    private final Map<UserId, User> users;
    private final Map<RoleId, Role> roles;
    private final KeyPairData keyPairData;
    private final Map<ClientId, Client> clients;
    private final Map<PermissionId, Permission> permissions;

    public Project(ProjectId id, String name, OrganizationId organizationId, PrivateKey organizationPrivateKey) throws PKIException {
        this.id = id;
        this.name = name;
        this.users = new ConcurrentHashMap<>();
        this.organizationId = organizationId;
        this.roles = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.permissions = new ConcurrentHashMap<>();
        this.keyPairData = TokenUtils.createSignedKeyPairData(organizationId.getId(), id.getId(), 10*365L, TimeUnit.DAYS, organizationPrivateKey);
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

    public void add(User user) {
        users.put(user.getId(), user);
    }

    public Collection<User> getAllUsers() {
        return users.values().stream()
                .filter(user -> user.getProjectId().equals(id))
                .collect(Collectors.toList());
    }

    public boolean remove(UserId userId) {
        return users.remove(userId) != null;
    }

    public Optional<User> getUser(UserId userId) {
        return Optional.ofNullable(users.get(userId));
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

    public void addClient(Client client) {
        clients.put(client.getId(), client);
    }

    public Optional<Client> getClient(ClientId id) {
        return Optional.ofNullable(clients.get(id));
    }

    public boolean removeClient(ClientId id) {
        return clients.remove(id) != null;
    }

    public Collection<Client> getClients() {
        return clients.values().stream().collect(Collectors.toList());
    }

    public boolean verifyClientCredentials(ClientCredentials clientCredentials) {
        return clientCredentials.equals(clients.get(clientCredentials.getId()).getCredentials());
    }

    public void addPermission(Permission permission) {
        this.permissions.put(permission.getId(), permission);
    }

    public Collection<Permission> getPermissions() {
        return this.permissions.values().stream().collect(Collectors.toList());
    }

    public boolean removePermission(PermissionId id) {
        return this.permissions.remove(id) != null;
    }

    public boolean addPermissionToRole(RoleId roleId, PermissionId permissionId) {
        Role role = roles.get(roleId);
        Permission permission = permissions.get(permissionId);
        if (role != null && permission != null) {
            role.addPermission(permission);
            return true;
        }
        return false;
    }

    public boolean removePermissionFromRole(RoleId roleId, PermissionId permissionId) {
        Role role = roles.get(roleId);
        Permission permission = permissions.get(permissionId);
        if (role != null && permission != null) {
            return role.removePermission(permission.getId());
        }
        return false;
    }

}
