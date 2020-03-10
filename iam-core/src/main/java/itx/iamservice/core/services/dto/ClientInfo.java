package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.security.cert.X509Certificate;

public class ClientInfo {

    private final ClientId id;
    private final ProjectId projectId;
    private final OrganizationId organizationId;
    private final String name;
    private final X509Certificate clientCertificate;

    public ClientInfo(ClientId id, ProjectId projectId, OrganizationId organizationId, String name, X509Certificate clientCertificate) {
        this.id = id;
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.clientCertificate = clientCertificate;
    }

    public ClientId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public X509Certificate getClientCertificate() {
        return clientCertificate;
    }

}
