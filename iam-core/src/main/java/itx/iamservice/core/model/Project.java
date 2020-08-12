package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface Project {

    ProjectId getId();

    String getName();

    Set<String> getAudience();

    OrganizationId getOrganizationId();

    Collection<UserId> getUsers();

    KeyPairSerialized getKeyPairSerialized();

    KeyPairData getKeyPairData();

    void add(UserId userId);

    boolean remove(UserId userId);

    void addRole(RoleId roleId);

    Collection<RoleId> getRoles();

    boolean removeRole(RoleId roleId);

    PrivateKey getPrivateKey();

    X509Certificate getCertificate();

    void addClient(ClientId id);

    boolean removeClient(ClientId id);

    Collection<ClientId> getClients();

    void addPermission(Permission permission);

    Collection<Permission> getPermissions();

    boolean removePermission(PermissionId id);

    Optional<Permission> getPermission(PermissionId id);

    Map<String, String> getProperties();

    void setProperty(String key, String value);

    void removeProperty(String key);

}
