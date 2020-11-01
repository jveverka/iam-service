package one.microproject.iamservice.core.services.dto;

import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;

import java.net.URI;
import java.util.Date;
import java.util.Set;

public class AuthorizationCodeContext {

    private final URI issuerUri;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final UserId userId;
    private final ClientId clientId;
    private final String state;
    private final Date issued;
    private final Scope scope;
    private final Set<String> audience;
    private final String redirectURI;

    public AuthorizationCodeContext(URI issuerUri, OrganizationId organizationId, ProjectId projectId, ClientId clientId, UserId userId,
                                    String state, Date issued, Scope scope, Set<String> audience, String redirectURI) {
        this.issuerUri = issuerUri;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.clientId = clientId;
        this.userId = userId;
        this.state = state;
        this.issued = issued;
        this.scope = scope;
        this.audience = audience;
        this.redirectURI = redirectURI;
    }

    public URI getIssuerUri() {
        return issuerUri;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getState() {
        return state;
    }

    public Date getIssued() {
        return issued;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public Scope getScope() {
        return scope;
    }

    public Set<String> getAudience() {
        return audience;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

}
