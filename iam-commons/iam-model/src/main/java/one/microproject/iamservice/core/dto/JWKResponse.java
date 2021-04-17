package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;

public class JWKResponse {

    private final Collection<JWKData> keys;

    public JWKResponse() {
        this.keys = Collections.emptyList();
    }

    @JsonCreator
    public JWKResponse(@JsonProperty("keys") Collection<JWKData> keys) {
        this.keys = keys;
    }

    public Collection<JWKData> getKeys() {
        return keys;
    }

}
