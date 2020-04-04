package itx.iamservice.core.model;

public enum TokenType {

    BEARER("Bearer"),
    REFRESH("Refresh"),
    ID("ID");

    private final String type;

    TokenType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TokenType getTokenType(String typeName) {
        switch (typeName) {
            case "Bearer":
                return BEARER;
            case "Refresh":
                return REFRESH;
            case "ID":
                return ID;
            default:
                throw new UnsupportedOperationException("Unsupported token_type " + typeName);
        }
    }

}
