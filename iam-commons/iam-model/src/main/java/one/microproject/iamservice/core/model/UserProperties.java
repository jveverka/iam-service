package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class UserProperties {

    private final Map<String, String> properties;

    @JsonCreator
    public UserProperties(@JsonProperty("properties") Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public static UserProperties getDefault() {
        return new UserProperties(new HashMap<>());
    }
}
