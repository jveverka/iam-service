package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.security.cert.X509Certificate;
import java.util.Collection;

public class ProjectInfo {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final X509Certificate organizationCertificate;
    private final X509Certificate projectCertificate;
    private final Collection<Client> clientCredentials;

    public ProjectInfo(ProjectId id, OrganizationId organizationId, String name, X509Certificate organizationCertificate, X509Certificate projectCertificate, Collection<Client> clientCredentials) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = organizationCertificate;
        this.projectCertificate = projectCertificate;
        this.clientCredentials = clientCredentials;
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

    public X509Certificate getOrganizationCertificate() {
        return organizationCertificate;
    }

    public X509Certificate getProjectCertificate() {
        return projectCertificate;
    }

    public Collection<Client> getClientCredentials() {
        return clientCredentials;
    }
}
