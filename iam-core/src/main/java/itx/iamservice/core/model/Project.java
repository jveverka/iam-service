package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface Project {

    ProjectId getId();

    String getName();

    OrganizationId getOrganizationId();

    Collection<User> getUsers();

    KeyPairSerialized getKeyPairSerialized();

    void add(User user);

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
