package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.KeyPairData;

import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Set;

public class UserInfo {

    private final String id;
    private final String organizationId;
    private final String projectId;
    private final String name;
    private final CertificateInfo organizationCertificate;
    private final CertificateInfo projectCertificate;
    private final CertificateInfo userCertificate;
    private final Set<String> roles;
    private final Set<String> permissions;

    public UserInfo(String id, String projectId, String organizationId, String name,
                    KeyPairData organizationKeyPairData, KeyPairData projectKeyPairData, KeyPairData userKeyPairData,
                    Set<String> roles, Set<String> permissions) throws CertificateEncodingException {
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
        this.permissions = permissions;
    }

    @JsonCreator
    public UserInfo(@JsonProperty("id") String id,
                    @JsonProperty("projectId") String projectId,
                    @JsonProperty("organizationId") String organizationId,
                    @JsonProperty("name") String name,
                    @JsonProperty("organizationCertificate") CertificateInfo organizationCertificate,
                    @JsonProperty("projectCertificate") CertificateInfo projectCertificate,
                    @JsonProperty("userCertificate") CertificateInfo userCertificate,
                    @JsonProperty("roles") Set<String> roles,
                    @JsonProperty("permissions") Set<String> permissions) {
        this.id = id;
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = organizationCertificate;
        this.projectCertificate = projectCertificate;
        this.userCertificate = userCertificate;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getOrganizationId() {
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

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

}
