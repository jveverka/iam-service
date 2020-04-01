package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;

import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Set;

public class UserInfo {

    private final UserId id;
    private final ProjectId projectId;
    private final OrganizationId organizationId;
    private final String name;
    private final CertificateInfo organizationCertificate;
    private final CertificateInfo projectCertificate;
    private final CertificateInfo userCertificate;
    private final Set<RoleId> roles;

    public UserInfo(UserId id, ProjectId projectId, OrganizationId organizationId, String name,
                    KeyPairData organizationKeyPairData, KeyPairData projectKeyPairData, KeyPairData userKeyPairData,
                    Set<RoleId> roles) throws CertificateEncodingException {
        this.id = id;
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = new CertificateInfo(organizationKeyPairData.getId().getId(),
                Base64.getEncoder().encodeToString(organizationKeyPairData.getX509Certificate().getEncoded()));
        this.projectCertificate = new CertificateInfo(projectKeyPairData.getId().getId(),
                Base64.getEncoder().encodeToString(projectKeyPairData.getX509Certificate().getEncoded()));
        this.userCertificate = new CertificateInfo(userKeyPairData.getId().getId(),
                Base64.getEncoder().encodeToString(userKeyPairData.getX509Certificate().getEncoded()));
        this.roles = roles;
    }

    @JsonCreator
    public UserInfo(@JsonProperty("id") UserId id,
                    @JsonProperty("projectId") ProjectId projectId,
                    @JsonProperty("organizationId") OrganizationId organizationId,
                    @JsonProperty("name") String name,
                    @JsonProperty("organizationCertificate") CertificateInfo organizationCertificate,
                    @JsonProperty("projectCertificate") CertificateInfo projectCertificate,
                    @JsonProperty("userCertificate") CertificateInfo userCertificate,
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

    public CertificateInfo getUserCertificate() {
        return userCertificate;
    }

    public CertificateInfo getOrganizationCertificate() {
        return organizationCertificate;
    }

    public CertificateInfo getProjectCertificate() {
        return projectCertificate;
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

}
