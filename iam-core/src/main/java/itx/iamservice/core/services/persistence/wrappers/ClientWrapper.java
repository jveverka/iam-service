package itx.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.keys.ModelKey;

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
