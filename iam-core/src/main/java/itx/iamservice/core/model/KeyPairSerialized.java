package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class KeyPairSerialized {

    private final KeyPairId id;
    private final String privateKey;
    private final String x509Certificate;

    @JsonCreator
    public KeyPairSerialized(@JsonProperty("id") KeyPairId id,
                             @JsonProperty("privateKey") String privateKey,
                             @JsonProperty("x509Certificate") String x509Certificate) {
        this.id = id;
        this.privateKey = privateKey;
        this.x509Certificate = x509Certificate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getX509Certificate() {
        return x509Certificate;
    }

    public KeyPairId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyPairSerialized that = (KeyPairSerialized) o;
        return Objects.equals(privateKey, that.privateKey) &&
                Objects.equals(x509Certificate, that.x509Certificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privateKey, x509Certificate);
    }

}
