package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.security.cert.X509Certificate;

public class ProjectInfo {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final X509Certificate organizationCertificate;
    private final X509Certificate projectCertificate;

    public ProjectInfo(ProjectId id, OrganizationId organizationId, String name, X509Certificate organizationCertificate, X509Certificate projectCertificate) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = organizationCertificate;
        this.projectCertificate = projectCertificate;
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

}
