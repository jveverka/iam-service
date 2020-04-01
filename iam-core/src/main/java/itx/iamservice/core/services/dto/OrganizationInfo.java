package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Set;

public class OrganizationInfo {

    private final OrganizationId organizationId;
    private final String name;
    private final Set<ProjectId> projects;
    private final CertificateInfo x509Certificate;

    public OrganizationInfo(OrganizationId organizationId, String name, Set<ProjectId> projects,
                            KeyPairData keyPairData) throws CertificateEncodingException {
        this.organizationId = organizationId;
        this.name = name;
        this.projects = projects;
        this.x509Certificate = new CertificateInfo(keyPairData.getId().getId(),
                Base64.getEncoder().encodeToString(keyPairData.getX509Certificate().getEncoded()));
    }

    @JsonCreator
    public OrganizationInfo(@JsonProperty("organizationId") OrganizationId organizationId,
                            @JsonProperty("name") String name,
                            @JsonProperty("projects") Set<ProjectId> projects,
                            @JsonProperty("x509Certificate") CertificateInfo x509Certificate) {
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

    public CertificateInfo getX509Certificate() {
        return x509Certificate;
    }

}
