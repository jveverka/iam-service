package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserImpl.class, name = "iam-user") }
)
public interface User {

    UserId getId();

    String getName();

    ProjectId getProjectId();

    KeyPairSerialized getKeyPairSerialized();

    KeyPairData getKeyPairData();

    Collection<Credentials> getCredentials();

    void addRole(RoleId roleId);

    void addCredentials(Credentials credentials);

    Optional<Credentials> getCredentials(Class<? extends Credentials> type);

    PrivateKey getPrivateKey();

    X509Certificate getCertificate();

    Long getDefaultAccessTokenDuration();

    Long getDefaultRefreshTokenDuration();

    Set<RoleId> getRoles();

    boolean removeRole(RoleId roleId);

    String getEmail();

    UserProperties getProperties();

}
