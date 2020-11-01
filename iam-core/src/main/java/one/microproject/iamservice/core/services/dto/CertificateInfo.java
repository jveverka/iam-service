package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CertificateInfo {

    private final String id;
    private final String x509Certificate;

    @JsonCreator
    public CertificateInfo(@JsonProperty("id") String id,
                           @JsonProperty("x509Certificate") String x509Certificate) {
        this.id = id;
        this.x509Certificate = x509Certificate;
    }

    public String getId() {
        return id;
    }

    public String getX509Certificate() {
        return x509Certificate;
    }

}
