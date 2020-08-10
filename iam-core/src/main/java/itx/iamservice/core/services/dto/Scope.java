package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class Scope {

    private final Set<String> values;

    @JsonCreator
    public Scope(@JsonProperty("values") Set<String> values) {
        this.values = values;
    }

    public Set<String> getValues() {
        return values;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return values.isEmpty();
    }

}
