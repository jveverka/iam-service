package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientImpl.class, name = "iam-client") }
        )
public interface Client {

    ClientId getId();

    String getName();

    ClientCredentials getCredentials();

    Set<RoleId> getRoles();

    boolean addRole(RoleId roleId);

    boolean removeRole(RoleId roleId);

    Long getDefaultAccessTokenDuration();

    Long getDefaultRefreshTokenDuration();

    ClientProperties getProperties();

}
