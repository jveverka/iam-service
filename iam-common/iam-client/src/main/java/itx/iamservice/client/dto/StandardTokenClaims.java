package itx.iamservice.client.dto;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

public class StandardTokenClaims {

    private final String keyId;
    private final String issuer;
    private final String subject;
    private final Set<String> audience;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final URI issuerUri;
    private final Set<String> scope;

    public StandardTokenClaims(String keyId, String issuer, String subject, Set<String> audience, Set<String> scope,
                               OrganizationId organizationId, ProjectId projectId) throws URISyntaxException {
        this.keyId = keyId;
        this.issuer = issuer;
        this.subject = subject;
        this.audience = audience;
        this.scope = scope;
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

    public Set<String> getAudience() {
        return audience;
    }

    public Set<String> getScope() {
        return scope;
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
