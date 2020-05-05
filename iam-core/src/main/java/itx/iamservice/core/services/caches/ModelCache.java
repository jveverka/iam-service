package itx.iamservice.core.services.caches;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;

import java.util.Collection;
import java.util.Optional;

public interface ModelCache {

    Model getModel();

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
