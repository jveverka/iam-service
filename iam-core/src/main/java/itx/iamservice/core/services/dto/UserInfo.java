package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private final String organizationCertificate;
    private final String projectCertificate;
    private final String userCertificate;
    private final Set<RoleId> roles;

    public UserInfo(UserId id, ProjectId projectId, OrganizationId organizationId, String name,
                    X509Certificate organizationCertificate, X509Certificate projectCertificate, X509Certificate userCertificate,
                    Set<RoleId> roles) throws CertificateEncodingException {
        this.id = id;
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = Base64.getEncoder().encodeToString(organizationCertificate.getEncoded());
        this.projectCertificate = Base64.getEncoder().encodeToString(projectCertificate.getEncoded());
        this.userCertificate = Base64.getEncoder().encodeToString(userCertificate.getEncoded());
        this.roles = roles;
    }

    @JsonCreator
    public UserInfo(@JsonProperty("id") UserId id,
                    @JsonProperty("projectId") ProjectId projectId,
                    @JsonProperty("organizationId") OrganizationId organizationId,
                    @JsonProperty("name") String name,
                    @JsonProperty("organizationCertificate") String organizationCertificate,
                    @JsonProperty("projectCertificate") String projectCertificate,
                    @JsonProperty("userCertificate") String userCertificate,
                    @JsonProperty("roles") Set<RoleId> roles) {
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

    public String getUserCertificate() {
        return userCertificate;
    }

    public String getOrganizationCertificate() {
        return organizationCertificate;
    }

    public String getProjectCertificate() {
        return projectCertificate;
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

}
