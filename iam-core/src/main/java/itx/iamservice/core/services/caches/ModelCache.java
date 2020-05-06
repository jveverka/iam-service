package itx.iamservice.core.services.caches;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
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

    Optional<Organization> getOrganization(OrganizationId organizationId);

    Collection<Organization> getOrganizations();

    boolean remove(OrganizationId organizationId);

    //PROJECT
    void add(OrganizationId organizationId, Project  project);

    Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId);

    Collection<Project> getProjects(OrganizationId organizationId);

    boolean remove(OrganizationId organizationId, ProjectId projectId);

    //USER
    Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId);

    //CLIENT
    void add(OrganizationId organizationId, ProjectId projectId, Client client);

    Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

    Collection<Client> getClients(OrganizationId organizationId, ProjectId projectId);

    boolean verifyClientCredentials(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials);

    boolean remove(OrganizationId organizationId, ProjectId projectId, ClientId clientId);
    //ROLE

}
