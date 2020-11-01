package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JWKData {

    @JsonProperty("kid")
    private final String keyId;

    @JsonProperty("kty")
    private final String keyType;

    @JsonProperty("use")
    private final String use;

    @JsonProperty("alg")
    private final String algorithm;

    @JsonProperty("key_ops")
    private final String[] keyOperations;

    @JsonProperty("x5t#S256")
    private final String x509CertificateSHA256Thumbprint;

    @JsonProperty("n")
    private final String modulusValue;

    @JsonProperty("e")
    private final String exponentValue;

    @JsonCreator
    public JWKData(@JsonProperty("kid") String keyId,
                   @JsonProperty("kty") String keyType,
                   @JsonProperty("use") String use,
                   @JsonProperty("alg") String algorithm,
                   @JsonProperty("key_ops") String[] keyOperations,
                   @JsonProperty("x5t#S256") String x509CertificateSHA256Thumbprint,
                   @JsonProperty("n") String modulusValue,
                   @JsonProperty("e") String exponentValue) {
        this.keyId = keyId;
        this.keyType = keyType;
        this.use = use;
        this.algorithm = algorithm;
        this.keyOperations = keyOperations;
        this.x509CertificateSHA256Thumbprint = x509CertificateSHA256Thumbprint;
        this.modulusValue  = modulusValue;
        this.exponentValue = exponentValue;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getUse() {
        return use;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String[] getKeyOperations() {
        return keyOperations;
    }

    public String getX509CertificateSHA256Thumbprint() {
        return x509CertificateSHA256Thumbprint;
    }

    public String getModulusValue() {
        return modulusValue;
    }

    public String getExponentValue() {
        return exponentValue;
    }

}
