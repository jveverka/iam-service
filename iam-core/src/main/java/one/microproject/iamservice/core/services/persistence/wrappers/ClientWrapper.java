package one.microproject.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.keys.ModelKey;

public class ClientWrapper {

    private final ModelKey<Client> key;
    private final Client value;

    @JsonCreator
    public ClientWrapper(@JsonProperty("key") ModelKey<Client> key,
                         @JsonProperty("value") Client value) {
        this.key = key;
        this.value = value;
    }

    public ModelKey<Client> getKey() {
        return key;
    }

    public Client getValue() {
        return value;
    }

}
