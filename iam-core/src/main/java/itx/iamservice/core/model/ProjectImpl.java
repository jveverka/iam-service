package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.utils.TokenUtils;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProjectImpl implements Project {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final Set<UserId> users;
    private final Set<RoleId> roles;
    private final KeyPairData keyPairData;
    private final KeyPairSerialized keyPairSerialized;
    private final Set<ClientId> clients;
    private final Set<Permission> permissions;
    private final Set<String> audience;

    public ProjectImpl(ProjectId id, String name, OrganizationId organizationId, PrivateKey organizationPrivateKey, Collection<String> audience) throws PKIException {
        this.id = id;
        this.name = name;
        this.users = new HashSet<>();
        this.organizationId = organizationId;
        this.roles = new HashSet<>();
        this.clients = new HashSet<>();
        this.permissions = new HashSet<>();
        this.keyPairData = TokenUtils.createSignedKeyPairData(organizationId.getId(), id.getId(), 10*365L, TimeUnit.DAYS, organizationPrivateKey);
        this.keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);
        this.audience = new HashSet<>();
        audience.forEach(a->
                this.audience.add(a)
        );
    }

    @JsonCreator
    public ProjectImpl(@JsonProperty("id") ProjectId id,
                       @JsonProperty("name") String name,
                       @JsonProperty("organizationId") OrganizationId organizationId,
                       @JsonProperty("keyPairSerialized") KeyPairSerialized keyPairSerialized,
                       @JsonProperty("users") Collection<UserId> users,
                       @JsonProperty("roles") Collection<RoleId> roles,
                       @JsonProperty("permissions") Collection<Permission> permissions,
                       @JsonProperty("clients") Collection<ClientId> clients,
                       @JsonProperty("audience") Collection<String> audience) throws PKIException {
        this.id = id;
        this.name = name;
        this.users = new HashSet<>();
        this.organizationId = organizationId;
        this.roles = new HashSet<>();
        this.clients = new HashSet<>();
        this.permissions = new HashSet<>();
        this.audience = new HashSet<>();
        this.keyPairData = ModelUtils.deserializeKeyPair(keyPairSerialized);
        this.keyPairSerialized = keyPairSerialized;
        users.forEach(u->
            this.users.add(u)
        );
        roles.forEach(r->
            this.roles.add(r)
        );
        permissions.forEach(p->
            this.permissions.add(p)
        );
        clients.forEach(c->
            this.clients.add(c)
        );
        audience.forEach(a->
                this.audience.add(a)
        );
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
    public Set<String> getAudience() {
        return audience;
    }

    @Override
    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    @Override
    public Collection<UserId> getUsers() {
        return users.stream().collect(Collectors.toList());
    }

    @Override
    public KeyPairSerialized getKeyPairSerialized() {
        return keyPairSerialized;
    }

    @Override
    @JsonIgnore
    public KeyPairData getKeyPairData() {
        return keyPairData;
    }

    @Override
    public Collection<RoleId> getRoles() {
        return roles.stream().collect(Collectors.toList());
    }

    @Override
    public Collection<ClientId> getClients() {
        return clients.stream().collect(Collectors.toList());
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.permissions.stream().collect(Collectors.toList());
    }

    @Override
    public void add(UserId userId) {
        users.add(userId);
    }

    @Override
    public boolean remove(UserId userId) {
        return users.remove(userId);
    }

    @Override
    public void addRole(RoleId roleId) {
        roles.add(roleId);
    }

    @Override
    public boolean removeRole(RoleId id) {
        return roles.remove(id);
    }

    @Override
    @JsonIgnore
    public PrivateKey getPrivateKey() {
        return keyPairData.getPrivateKey();
    }

    @Override
    @JsonIgnore
    public X509Certificate getCertificate() {
        return keyPairData.getX509Certificate();
    }

    @Override
    public void addClient(ClientId id) {
        clients.add(id);
    }

    @Override
    public boolean removeClient(ClientId id) {
        return clients.remove(id);
    }

    @Override
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    @Override
    public boolean removePermission(PermissionId id) {
        return this.permissions.remove(id);
    }

    @Override
    public Optional<Permission> getPermission(PermissionId id) {
        return permissions.stream().filter(p->p.getId().equals(id)).findFirst();
    }

}
