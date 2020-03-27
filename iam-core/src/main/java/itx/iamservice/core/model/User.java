package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserImpl.class, name = "user") })
public interface User {

    UserId getId();

    String getName();

    ProjectId getProjectId();

    void addRole(RoleId roleId);

    void addCredentials(Credentials credentials);

    Optional<Credentials> getCredentials(Class<? extends CredentialsType> type);

    PrivateKey getPrivateKey();

    X509Certificate getCertificate();

    Long getDefaultAccessTokenDuration();

    Long getDefaultRefreshTokenDuration();

    Set<RoleId> getRoles();

    boolean removeRole(RoleId roleId);

}
