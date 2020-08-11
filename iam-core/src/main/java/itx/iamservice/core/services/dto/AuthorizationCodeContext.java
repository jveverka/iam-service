package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;

import java.util.Date;
import java.util.Set;

public class AuthorizationCodeContext {

    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final UserId userId;
    private final ClientId clientId;
    private final String state;
    private final Date issued;
    private final Scope scope;
    private final Set<String> audience;

    public AuthorizationCodeContext(OrganizationId organizationId, ProjectId projectId, ClientId clientId, UserId userId, String state, Date issued, Scope scope, Set<String> audience) {
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.clientId = clientId;
        this.userId = userId;
        this.state = state;
        this.issued = issued;
        this.scope = scope;
        this.audience = audience;
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
}
