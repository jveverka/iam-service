package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;

import java.net.URI;
import java.util.Date;
import java.util.Set;

public class AuthorizationCodeContext {

    private final Code code;
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
    private final String codeChallenge;
    private final String codeChallengeMethod;

    @JsonCreator
    public AuthorizationCodeContext(
            @JsonProperty("code") Code code,
            @JsonProperty("issuerUri") URI issuerUri,
            @JsonProperty("organizationId") OrganizationId organizationId,
            @JsonProperty("projectId") ProjectId projectId,
            @JsonProperty("clientId") ClientId clientId,
            @JsonProperty("userId") UserId userId,
            @JsonProperty("state") String state,
            @JsonProperty("issued") Date issued,
            @JsonProperty("scope") Scope scope,
            @JsonProperty("audience") Set<String> audience,
            @JsonProperty("redirectURI") String redirectURI,
            @JsonProperty("codeChallenge") String codeChallenge,
            @JsonProperty("codeChallengeMethod") String codeChallengeMethod) {
        this.code = code;
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
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public Code getCode() {
        return code;
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

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

}
