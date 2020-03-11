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

}
