package itx.iamservice.core.services.dto;

import java.util.Objects;

public final class AuthorizationCode {

    private final Code code;
    private final String state;

    public AuthorizationCode(Code code, String state) {
        this.code = code;
        this.state = state;
    }

    public Code getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationCode that = (AuthorizationCode) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, state);
    }

    @Override
    public String toString() {
        return "code=" + code + ", state=" + state;
    }

}
