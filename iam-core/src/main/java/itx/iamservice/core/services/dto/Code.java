package itx.iamservice.core.services.dto;

import java.util.Objects;

public class Code {

    private final String code;

    public Code(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Code from(String code) {
        return new Code(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Code code1 = (Code) o;
        return Objects.equals(code, code1.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
