package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
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

    public static Scope empty() {
        return new Scope(Set.of());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scope scope = (Scope) o;
        return Objects.equals(values, scope.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
