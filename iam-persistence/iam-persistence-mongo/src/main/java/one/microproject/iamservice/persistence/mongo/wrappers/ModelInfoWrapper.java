package one.microproject.iamservice.persistence.mongo.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Model;

public class ModelInfoWrapper {

    private final String _id;
    private final Model model;

    @JsonCreator
    public ModelInfoWrapper(@JsonProperty("_id") String _id, @JsonProperty("model") Model model) {
        this._id = _id;
        this.model = model;
    }

    public String get_id() {
        return _id;
    }

    public Model getModel() {
        return model;
    }

}
