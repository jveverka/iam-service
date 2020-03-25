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

public class ProjectImpl implements Project {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final Map<UserId, User> users;
    private final Map<RoleId, Role> roles;
    private final KeyPairData keyPairData;
    private final Map<ClientId, Client> clients;
    private final Map<PermissionId, Permission> permissions;

    public ProjectImpl(ProjectId id, String name, OrganizationId organizationId, PrivateKey organizationPrivateKey) throws PKIException {
        this.id = id;
        this.name = name;
        this.users = new ConcurrentHashMap<>();
        this.organizationId = organizationId;
        this.roles = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.permissions = new ConcurrentHashMap<>();
        this.keyPairData = TokenUtils.createSignedKeyPairData(organizationId.getId(), id.getId(), 10*365L, TimeUnit.DAYS, organizationPrivateKey);
    }

    @Override
    public ProjectId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    @Override
    public void add(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values().stream()
                .filter(user -> user.getProjectId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public boolean remove(UserId userId) {
        return users.remove(userId) != null;
    }

    @Override
    public Optional<User> getUser(UserId userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void addRole(Role role) {
        roles.put(role.getId(), role);
    }

    @Override
    public Optional<Role> getRole(RoleId id) {
        return Optional.ofNullable(roles.get(id));
    }

    @Override
    public Collection<Role> getRoles() {
        return roles.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean removeRole(RoleId id) {
        return roles.remove(id) != null;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return keyPairData.getPrivateKey();
    }

    @Override
    public X509Certificate getCertificate() {
        return keyPairData.getX509Certificate();
    }

    @Override
    public void addClient(Client client) {
        clients.put(client.getId(), client);
    }

    @Override
    public Optional<Client> getClient(ClientId id) {
        return Optional.ofNullable(clients.get(id));
    }

    @Override
    public boolean removeClient(ClientId id) {
        return clients.remove(id) != null;
    }

    @Override
    public Collection<Client> getClients() {
        return clients.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean verifyClientCredentials(ClientCredentials clientCredentials) {
        return clientCredentials.equals(clients.get(clientCredentials.getId()).getCredentials());
    }

    @Override
    public void addPermission(Permission permission) {
        this.permissions.put(permission.getId(), permission);
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.permissions.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean removePermission(PermissionId id) {
        return this.permissions.remove(id) != null;
    }

    @Override
    public boolean addPermissionToRole(RoleId roleId, PermissionId permissionId) {
        Role role = roles.get(roleId);
        Permission permission = permissions.get(permissionId);
        if (role != null && permission != null) {
            role.addPermission(permission);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePermissionFromRole(RoleId roleId, PermissionId permissionId) {
        Role role = roles.get(roleId);
        Permission permission = permissions.get(permissionId);
        if (role != null && permission != null) {
            return role.removePermission(permission.getId());
        }
        return false;
    }

}
