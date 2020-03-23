package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
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
    private final X509Certificate organizationCertificate;
    private final X509Certificate projectCertificate;
    private final Collection<Client> clients;
    private final Set<UserId> users;

    public ProjectInfo(ProjectId id, OrganizationId organizationId, String name,
                       X509Certificate organizationCertificate, X509Certificate projectCertificate,
                       Collection<Client> clients, Set<UserId> users) {
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

    public String getOrganizationCertificate() throws CertificateEncodingException {
        return Base64.getEncoder().encodeToString(organizationCertificate.getEncoded());
    }

    public String getProjectCertificate() throws CertificateEncodingException {
        return Base64.getEncoder().encodeToString(projectCertificate.getEncoded());
    }

    public Collection<Client> getClients() {
        return clients;
    }

    public Set<UserId> getUsers() {
        return users;
    }

}
