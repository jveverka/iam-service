package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProviderConfigurationResponse {

    @JsonProperty("issuer")
    private final String issuer;

    @JsonProperty("authorization_endpoint")
    private final String authorizationEndpoint;

    @JsonProperty("token_endpoint")
    private final String tokenEndpoint;

    @JsonProperty("jwks_uri")
    private final String jwksUri;

    @JsonProperty("scopes_supported")
    private final String[] scopesSupported;

    @JsonProperty("response_types_supported")
    private final String[] responseTypesSupported;

    @JsonProperty("grant_types_supported")
    private final String[] grantTypesSupported;

    @JsonProperty("subject_types_supported")
    private final String[] subjectTypesSupported;

    @JsonProperty("id_token_signing_alg_values_supported")
    private final String[] idTokenSigningAlgValuesSupported;

    @JsonProperty("id_token_encryption_alg_values_supported")
    private final String[] idTokenEncryptionAlgValuesSupported;

    @JsonProperty("introspection_endpoint")
    private final String introspectionEndpoint;

    @JsonProperty("revocation_endpoint")
    private final String revocationEndpoint;

    @JsonCreator
    public ProviderConfigurationResponse(@JsonProperty("issuer") String issuer,
                                         @JsonProperty("authorization_endpoint") String authorizationEndpoint,
                                         @JsonProperty("token_endpoint") String tokenEndpoint,
                                         @JsonProperty("jwks_uri") String jwksUri,
                                         @JsonProperty("scopes_supported") String[] scopesSupported,
                                         @JsonProperty("response_types_supported") String[] responseTypesSupported,
                                         @JsonProperty("grant_types_supported") String[] grantTypesSupported,
                                         @JsonProperty("subject_types_supported") String[] subjectTypesSupported,
                                         @JsonProperty("id_token_signing_alg_values_supported") String[] idTokenSigningAlgValuesSupported,
                                         @JsonProperty("id_token_encryption_alg_values_supported") String[] idTokenEncryptionAlgValuesSupported,
                                         @JsonProperty("introspection_endpoint") String introspectionEndpoint,
                                         @JsonProperty("revocation_endpoint") String revocationEndpoint) {
        this.issuer = issuer;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.jwksUri = jwksUri;
        this.scopesSupported = scopesSupported;
        this.responseTypesSupported = responseTypesSupported;
        this.grantTypesSupported = grantTypesSupported;
        this.subjectTypesSupported = subjectTypesSupported;
        this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
        this.idTokenEncryptionAlgValuesSupported = idTokenEncryptionAlgValuesSupported;
        this.introspectionEndpoint = introspectionEndpoint;
        this.revocationEndpoint = revocationEndpoint;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public String[] getScopesSupported() {
        return scopesSupported;
    }

    public String[] getResponseTypesSupported() {
        return responseTypesSupported;
    }

    public String[] getSubjectTypesSupported() {
        return subjectTypesSupported;
    }

    public String[] getIdTokenSigningAlgValuesSupported() {
        return idTokenSigningAlgValuesSupported;
    }

    public String[] getIdTokenEncryptionAlgValuesSupported() {
        return idTokenEncryptionAlgValuesSupported;
    }

    public String[] getGrantTypesSupported() {
        return grantTypesSupported;
    }

    public String getIntrospectionEndpoint() {
        return introspectionEndpoint;
    }

    public String getRevocationEndpoint() {
        return revocationEndpoint;
    }

}
