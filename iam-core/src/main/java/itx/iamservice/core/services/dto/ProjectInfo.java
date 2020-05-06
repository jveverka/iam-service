package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;

import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Collection;
import java.util.Set;

public class ProjectInfo {

    private final ProjectId id;
    private final OrganizationId organizationId;
    private final String name;
    private final CertificateInfo organizationCertificate;
    private final CertificateInfo projectCertificate;
    private final Collection<ClientId> clients;
    private final Set<UserId> users;

    public ProjectInfo(ProjectId id, OrganizationId organizationId, String name,
                       KeyPairData organizationKeyPairData, KeyPairData projectKeyPairData,
                       Collection<ClientId> clients, Set<UserId> users) throws CertificateEncodingException {
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
    public ProjectInfo(@JsonProperty("id") ProjectId id,
                       @JsonProperty("organizationId") OrganizationId organizationId,
                       @JsonProperty("name") String name,
                       @JsonProperty("organizationCertificate") CertificateInfo organizationCertificate,
                       @JsonProperty("projectCertificate") CertificateInfo projectCertificate,
                       @JsonProperty("clients") Collection<ClientId> clients,
                       @JsonProperty("users") Set<UserId> users) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.organizationCertificate = organizationCertificate;
        this.projectCertificate = projectCertificate;
        this.clients = clients;
        this.users = users;
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

    public CertificateInfo getOrganizationCertificate() {
        return organizationCertificate;
    }

    public CertificateInfo getProjectCertificate() {
        return projectCertificate;
    }

    public Collection<ClientId> getClients() {
        return clients;
    }

    public Set<UserId> getUsers() {
        return users;
    }

}
