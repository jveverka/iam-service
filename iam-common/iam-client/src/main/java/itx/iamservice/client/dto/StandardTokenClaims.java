package itx.iamservice.client.dto;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class StandardTokenClaims {

    private final String keyId;
    private final String issuer;
    private final String subject;
    private final List<String> audience;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final URI issuerUri;

    public StandardTokenClaims(String keyId, String issuer, String subject, List<String> audience, OrganizationId organizationId, ProjectId projectId) throws URISyntaxException {
        this.keyId = keyId;
        this.issuer = issuer;
        this.subject = subject;
        this.audience = audience;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.issuerUri = new URI(issuer);
    }

    public String getKeyId() {
        return keyId;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getAudience() {
        return audience;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public URI getIssuerUri() {
        return issuerUri;
    }

}
