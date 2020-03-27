package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collection;
import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ModelImpl.class, name = "model") })
public interface Model {

    ModelId getId();

    String getName();

    void add(Organization organization);

    Collection<Organization> getOrganizations();

    Optional<Organization> getOrganization(OrganizationId organizationId);

    boolean remove(OrganizationId organizationId);

    Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId);

    Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId);

    Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

}
