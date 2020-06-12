package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientImpl.class, name = "client") })
public interface Client {

    ClientId getId();

    String getName();

    ClientCredentials getCredentials();

    Set<RoleId> getRoles();

    boolean addRole(RoleId roleId);

    boolean removeRole(RoleId roleId);

    Long getDefaultAccessTokenDuration();

    Long getDefaultRefreshTokenDuration();

}
