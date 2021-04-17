package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Code {

    @JsonProperty("code")
    private final String codeValue;

    @JsonCreator
    public Code(@JsonProperty("code") String codeValue) {
        this.codeValue = codeValue;
    }

    @JsonProperty("code")
    public String getCodeValue() {
        return codeValue;
    }

    public static Code from(String codeValue) {
        return new Code(codeValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Code code = (Code) o;
        return Objects.equals(codeValue, code.codeValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeValue);
    }

    @Override
    public String toString() {
        return codeValue;
    }
}
