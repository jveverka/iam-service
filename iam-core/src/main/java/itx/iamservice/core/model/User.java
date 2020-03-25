package itx.iamservice.core.model;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.Set;

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
