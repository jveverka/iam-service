package one.microproject.iamservice.core.model.keys;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.KeyId;
import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientId.class, name = "ClientId"),
        @JsonSubTypes.Type(value = KeyId.class, name = "KeyId"),
        @JsonSubTypes.Type(value = OrganizationId.class, name = "OrganizationId"),
        @JsonSubTypes.Type(value = ProjectId.class, name = "ProjectId"),
        @JsonSubTypes.Type(value = PermissionId.class, name = "PermissionId"),
        @JsonSubTypes.Type(value = RoleId.class, name = "RoleId"),
        @JsonSubTypes.Type(value = UserId.class, name = "UserId"),
        @JsonSubTypes.Type(value = ModelId.class, name = "ModelId")
})
public abstract class Id {

    private final String id;

    @JsonCreator
    protected Id(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id that = (Id) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

}
