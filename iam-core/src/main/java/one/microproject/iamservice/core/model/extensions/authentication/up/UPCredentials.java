package one.microproject.iamservice.core.model.extensions.authentication.up;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UPCredentials implements Credentials<UPAuthenticationRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(UPCredentials.class);

    private final UserId userId;
    private final String salt;
    private final String hash;

    public UPCredentials(UserId userId,
                         String password) throws PKIException {
        try {
            this.userId = userId;
            this.salt = UUID.randomUUID().toString();
            this.hash = generateHash(salt, password);
        } catch (Exception e) {
            throw new PKIException(e);
        }
    }

    @JsonCreator
    public UPCredentials(@JsonProperty("userId") UserId userId,
                         @JsonProperty("salt") String salt,
                         @JsonProperty("hash") String hash) {
        this.userId = userId;
        this.salt = salt;
        this.hash = hash;
    }

    @Override
    public UserId getUserId() {
        return userId;
    }

    @Override
    public Class<UPCredentials> getType() {
        return UPCredentials.class;
    }

    public String getSalt() {
        return salt;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean verify(UPAuthenticationRequest authenticationRequest) {
        try {
            String generatedHash = generateHash(salt, authenticationRequest.getPassword());
            return userId.equals(authenticationRequest.getUserId()) && this.hash.equals(generatedHash);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Error generating hash: ", e);
            return false;
        }
    }

    private String generateHash(String salt, String password) throws NoSuchAlgorithmException {
        return ModelUtils.getSha512HashBase64(salt + "." + password);
    }

}
