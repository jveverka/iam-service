package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collection;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RoleImpl.class, name = "iam-role") }
        )
public interface Role {

    RoleId getId();

    String getName();

    void addPermission(Permission permission);

    Collection<Permission> getPermissions();

    boolean removePermission(PermissionId id);

}
