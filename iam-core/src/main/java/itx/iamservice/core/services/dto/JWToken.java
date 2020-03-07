package itx.iamservice.core.services.dto;

import java.util.Objects;

public final class JWToken {

    private final String token;

    public JWToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static JWToken from(String jwt) {
        return new JWToken(jwt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JWToken jwToken = (JWToken) o;
        return Objects.equals(token, jwToken.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
