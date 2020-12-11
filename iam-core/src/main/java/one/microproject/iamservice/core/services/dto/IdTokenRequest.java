package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IdTokenRequest {

    private final String issuerURL;
    private final String nonce;
    private final String codeVerifier;

    @JsonCreator
    public IdTokenRequest(@JsonProperty("issuerURL") String issuerURL,
                          @JsonProperty("nonce") String nonce,
                          @JsonProperty("codeVerifier") String codeVerifier) {
        this.issuerURL = issuerURL;
        this.nonce = nonce;
        this.codeVerifier = codeVerifier;
    }

    public String getIssuerURL() {
        return issuerURL;
    }

    public String getNonce() {
        return nonce;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

}
