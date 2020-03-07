package itx.iamservice.core.services.dto;

public class JWToken {

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

}
