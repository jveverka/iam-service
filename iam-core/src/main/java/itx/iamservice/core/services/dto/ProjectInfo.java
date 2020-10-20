package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.KeyPairData;

import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Set;

public class ProjectInfo {

    private final String id;
    private final String organizationId;
    private final String name;
    private final CertificateInfo organizationCertificate;
    private final CertificateInfo projectCertificate;
    private final Set<String> clients;
    private final Set<String> users;

    public ProjectInfo(String id, String organizationId, String name,
                       KeyPairData organizationKeyPairData, KeyPairData projectKeyPairData,
                       Set<String> clients, Set<String> users) throws CertificateEncodingException {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = new CertificateInfo(organizationKeyPairData.getId().getId(),
                Base64.getEncoder().encodeToString(organizationKeyPairData.getX509Certificate().getEncoded()));
        this.projectCertificate = new CertificateInfo(projectKeyPairData.getId().getId(),
                Base64.getEncoder().encodeToString(projectKeyPairData.getX509Certificate().getEncoded()));
        this.clients = clients;
        this.users = users;
    }

    @JsonCreator
    public ProjectInfo(@JsonProperty("id") String id,
                       @JsonProperty("organizationId") String organizationId,
                       @JsonProperty("name") String name,
                       @JsonProperty("organizationCertificate") CertificateInfo organizationCertificate,
                       @JsonProperty("projectCertificate") CertificateInfo projectCertificate,
                       @JsonProperty("clients") Set<String> clients,
                       @JsonProperty("users") Set<String> users) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = organizationCertificate;
        this.projectCertificate = projectCertificate;
        this.clients = clients;
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public CertificateInfo getOrganizationCertificate() {
        return organizationCertificate;
    }

    public CertificateInfo getProjectCertificate() {
        return projectCertificate;
    }

    public Set<String> getClients() {
        return clients;
    }

    public Set<String> getUsers() {
        return users;
    }

}
