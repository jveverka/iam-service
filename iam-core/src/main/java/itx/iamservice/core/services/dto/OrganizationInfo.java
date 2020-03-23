package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Set;

public class OrganizationInfo {

    private final OrganizationId organizationId;
    private final String name;
    private final Set<ProjectId> projects;
    private final X509Certificate x509Certificate;

    public OrganizationInfo(OrganizationId organizationId, String name, Set<ProjectId> projects,
                            X509Certificate x509Certificate) {
        this.organizationId = organizationId;
        this.name = name;
        this.projects = projects;
        this.x509Certificate = x509Certificate;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public Set<ProjectId> getProjects() {
        return projects;
    }

    public String getX509Certificate() throws CertificateEncodingException {
        return Base64.getEncoder().encodeToString(x509Certificate.getEncoded());
    }

}
