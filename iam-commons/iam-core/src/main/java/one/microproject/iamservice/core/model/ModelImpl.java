package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static one.microproject.iamservice.core.utils.ModelUtils.MODEL_VERSION;

public class ModelImpl implements Model {

    private final ModelId id;
    private final String name;
    private final String version;

    @JsonCreator
    public ModelImpl(@JsonProperty("id") ModelId id,
                     @JsonProperty("name") String name,
                     @JsonProperty("version") String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public ModelImpl(ModelId id, String name) {
        this.id = id;
        this.name = name;
        this.version = MODEL_VERSION;
    }

    @Override
    public ModelId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

}
