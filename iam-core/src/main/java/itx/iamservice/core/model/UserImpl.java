package itx.iamservice.core.model;

import itx.iamservice.core.model.utils.TokenUtils;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UserImpl implements User {

    private final UserId id;
    private final ProjectId projectId;
    private final String name;
    private final Map<Class<? extends CredentialsType>, Credentials> credentials;
    private final Set<RoleId> roles;
    private final KeyPairData keyPairData;
    private final Long defaultAccessTokenDuration;
    private final Long defaultRefreshTokenDuration;

    public UserImpl(UserId id, String name, ProjectId projectId, Long defaultAccessTokenDuration, Long defaultRefreshTokenDuration, PrivateKey projectPrivateKey) throws PKIException {
        this.id = id;
        this.name = name;
        this.credentials = new ConcurrentHashMap<>();
        this.roles = new CopyOnWriteArraySet<>();
        this.projectId = projectId;
        this.keyPairData = TokenUtils.createSignedKeyPairData(projectId.getId(), id.getId(), 365L, TimeUnit.DAYS, projectPrivateKey);
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
    }

    @Override
    public UserId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Override
    public void addRole(RoleId roleId) {
        this.roles.add(roleId);
    }

    @Override
    public void addCredentials(Credentials credentials) {
        this.credentials.put(credentials.getType().getClass(), credentials);
    }

    @Override
    public Optional<Credentials> getCredentials(Class<? extends CredentialsType> type) {
        return Optional.ofNullable(credentials.get(type));
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
    public Long getDefaultAccessTokenDuration() {
        return defaultAccessTokenDuration;
    }

    @Override
    public Long getDefaultRefreshTokenDuration() {
        return defaultRefreshTokenDuration;
    }

    @Override
    public Set<RoleId> getRoles() {
        return this.roles.stream().collect(Collectors.toSet());
    }

    @Override
    public boolean removeRole(RoleId roleId) {
        return roles.remove(roleId);
    }

}
