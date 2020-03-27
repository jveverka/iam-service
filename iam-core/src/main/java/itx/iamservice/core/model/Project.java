package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProjectImpl.class, name = "project") })
public interface Project {

    ProjectId getId();

    String getName();

    OrganizationId getOrganizationId();

    void add(User user);

    Collection<User> getAllUsers();

    boolean remove(UserId userId);

    Optional<User> getUser(UserId userId);

    void addRole(Role role);

    Optional<Role> getRole(RoleId id);

    Collection<Role> getRoles();

    boolean removeRole(RoleId id);

    PrivateKey getPrivateKey();

    X509Certificate getCertificate();

    void addClient(Client client);

    Optional<Client> getClient(ClientId id);

    boolean removeClient(ClientId id);

    Collection<Client> getClients();

    boolean verifyClientCredentials(ClientCredentials clientCredentials);

    void addPermission(Permission permission);

    Collection<Permission> getPermissions();

    boolean removePermission(PermissionId id);

    boolean addPermissionToRole(RoleId roleId, PermissionId permissionId);

    boolean removePermissionFromRole(RoleId roleId, PermissionId permissionId);

}
