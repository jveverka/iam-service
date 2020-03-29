package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IdTokenRequest {

    private final String issuerURL;
    private final String nonce;

    @JsonCreator
    public IdTokenRequest(@JsonProperty("issuerURL") String issuerURL,
                          @JsonProperty("nonce") String nonce) {
        this.issuerURL = issuerURL;
        this.nonce = nonce;
    }

    public String getIssuerURL() {
        return issuerURL;
    }

    public String getNonce() {
        return nonce;
    }

}
