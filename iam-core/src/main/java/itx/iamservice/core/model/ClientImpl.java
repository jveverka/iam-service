package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClientImpl implements Client {

    private final ClientCredentials credentials;
    private final String name;
    private final Long defaultAccessTokenDuration;
    private final Long defaultRefreshTokenDuration;
    private final Set<RoleId> roles;

    public ClientImpl(ClientCredentials credentials,
                  String name,
                  Long defaultAccessTokenDuration,
                  Long defaultRefreshTokenDuration) {
        this.credentials = credentials;
        this.name = name;
        this.roles = new HashSet<>();
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
    }

    @JsonCreator
    public ClientImpl(@JsonProperty("credentials") ClientCredentials credentials,
                  @JsonProperty("name") String name,
                  @JsonProperty("defaultAccessTokenDuration") Long defaultAccessTokenDuration,
                  @JsonProperty("defaultRefreshTokenDuration") Long defaultRefreshTokenDuration,
                  @JsonProperty("roles") Collection<RoleId> roles) {
        this.credentials = credentials;
        this.name = name;
        this.roles = new HashSet<>();
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
        this.roles.addAll(roles);
    }

    @JsonIgnore
    @Override
    public ClientId getId() {
        return credentials.getId();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ClientCredentials getCredentials() {
        return credentials;
    }

    @Override
    public Set<RoleId> getRoles() {
        return roles;
    }

    @Override
    public boolean addRole(RoleId roleId) {
        return roles.add(roleId);
    }

    @Override
    public boolean removeRole(RoleId roleId) {
        return roles.remove(roleId);
    }

    @Override
    public Long getDefaultAccessTokenDuration() {
        return defaultAccessTokenDuration;
    }

    @Override
    public Long getDefaultRefreshTokenDuration() {
        return defaultRefreshTokenDuration;
    }

}
