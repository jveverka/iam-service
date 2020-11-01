package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelImpl implements Model {

    private final ModelId id;
    private final String name;

    @JsonCreator
    public ModelImpl(@JsonProperty("id") ModelId id,
                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ModelId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}
