package itx.iamservice.services.dto;

public enum GrantType {

    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    PASSWORD("password"),
    CLIENT_CREDENTIALS("client_credentials");

    private final String typeName;

    GrantType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static GrantType getGrantType(String typeName) {
        switch (typeName) {
            case "authorization_code":
                return AUTHORIZATION_CODE;
            case "refresh_token":
                return REFRESH_TOKEN;
            case "password":
                return PASSWORD;
            case "client_credentials":
                return CLIENT_CREDENTIALS;
            default:
                throw new UnsupportedOperationException("Unsupported grant_type " + typeName);
        }
    }

}
