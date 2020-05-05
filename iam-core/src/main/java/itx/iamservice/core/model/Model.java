package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collection;
import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface Model {

    ModelId getId();

    String getName();

    //ORGANIZATION
    void add(Organization organization);

    Collection<Organization> getOrganizations();

    Optional<Organization> getOrganization(OrganizationId organizationId);

    boolean remove(OrganizationId organizationId);

    //PROJECT
    Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId);

    Collection<Project> getProjects(OrganizationId organizationId);

    void add(OrganizationId organizationId, Project  project);

    boolean remove(OrganizationId organizationId, ProjectId projectId);

    //USER
    Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId);

    //CLIENT
    Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

    //ROLE

}
