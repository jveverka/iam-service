package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Set;

public class UserInfo {

    private final UserId id;
    private final ProjectId projectId;
    private final OrganizationId organizationId;
    private final String name;
    private final X509Certificate organizationCertificate;
    private final X509Certificate projectCertificate;
    private final X509Certificate userCertificate;
    private final Set<RoleId> roles;

    public UserInfo(UserId id, ProjectId projectId, OrganizationId organizationId, String name,
                    X509Certificate organizationCertificate, X509Certificate projectCertificate, X509Certificate userCertificate,
                    Set<RoleId> roles) {
        this.id = id;
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = organizationCertificate;
        this.projectCertificate = projectCertificate;
        this.userCertificate = userCertificate;
        this.roles = roles;
    }

    public UserId getId() {
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

    public String getUserCertificate() throws CertificateEncodingException {
        return Base64.getEncoder().encodeToString(userCertificate.getEncoded());
    }

    public String getOrganizationCertificate() throws CertificateEncodingException {
        return Base64.getEncoder().encodeToString(organizationCertificate.getEncoded());
    }

    public String getProjectCertificate() throws CertificateEncodingException {
        return Base64.getEncoder().encodeToString(projectCertificate.getEncoded());
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

}
