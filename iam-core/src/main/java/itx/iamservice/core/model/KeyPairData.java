package itx.iamservice.core.model;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class KeyPairData {

    private final KeyPairId id;
    private final PrivateKey privateKey;
    private final X509Certificate x509Certificate;

    public KeyPairData(KeyPairId id, PrivateKey privateKey, X509Certificate x509Certificate) {
        this.id = id;
        this.privateKey = privateKey;
        this.x509Certificate = x509Certificate;
    }

    public KeyPairId getId() {
        return id;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }

    public PublicKey getPublicKey() {
        return x509Certificate.getPublicKey();
    }

}
