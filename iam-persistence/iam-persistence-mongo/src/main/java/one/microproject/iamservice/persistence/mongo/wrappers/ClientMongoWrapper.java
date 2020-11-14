package one.microproject.iamservice.persistence.mongo.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.keys.ModelKey;

public class ClientMongoWrapper {

    private final String _id;
    private final ModelKey<Client> key;
    private final Client value;

    @JsonCreator
    public ClientMongoWrapper(@JsonProperty("_id") String _id,
                              @JsonProperty("key") ModelKey<Client> key,
                              @JsonProperty("value") Client value) {
        this._id = _id;
        this.key = key;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public ModelKey<Client> getKey() {
        return key;
    }

    public Client getValue() {
        return value;
    }

}
