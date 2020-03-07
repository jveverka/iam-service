package itx.iamservice.core.model;

public final class ClientId extends Id {

    public ClientId(String id) {
        super(id);
    }

    public static ClientId from(String id) {
        return new ClientId(id);
    }

}
