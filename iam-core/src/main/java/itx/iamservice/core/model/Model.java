package itx.iamservice.core.model;

import java.util.Collection;
import java.util.Optional;

public interface Model {

    void add(Organization organization);

    Collection<Organization> getOrganizations();

    Optional<Organization> getOrganization(OrganizationId organizationId);

    boolean remove(OrganizationId organizationId);

    Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId);

    Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId);

}
