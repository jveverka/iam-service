package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collection;
import java.util.Set;

public class ProjectInfo {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final String organizationCertificate;
    private final String projectCertificate;
    private final Collection<Client> clients;
    private final Set<UserId> users;

    public ProjectInfo(ProjectId id, OrganizationId organizationId, String name,
                       X509Certificate organizationCertificate, X509Certificate projectCertificate,
                       Collection<Client> clients, Set<UserId> users) throws CertificateEncodingException {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = Base64.getEncoder().encodeToString(organizationCertificate.getEncoded());
        this.projectCertificate = Base64.getEncoder().encodeToString(projectCertificate.getEncoded());
        this.clients = clients;
        this.users = users;
    }

    @JsonCreator
    public ProjectInfo(@JsonProperty("id") ProjectId id,
                       @JsonProperty("organizationId") OrganizationId organizationId,
                       @JsonProperty("name") String name,
                       @JsonProperty("organizationCertificate") String organizationCertificate,
                       @JsonProperty("projectCertificate") String projectCertificate,
                       @JsonProperty("clients") Collection<Client> clients,
                       @JsonProperty("users") Set<UserId> users) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = organizationCertificate;
        this.projectCertificate = projectCertificate;
        this.clients = clients;
        this.users = users;
    }

    public ProjectId getId() {
        return id;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getOrganizationCertificate() {
        return organizationCertificate;
    }

    public String getProjectCertificate() {
        return projectCertificate;
    }

    public Collection<Client> getClients() {
        return clients;
    }

    public Set<UserId> getUsers() {
        return users;
    }

}
