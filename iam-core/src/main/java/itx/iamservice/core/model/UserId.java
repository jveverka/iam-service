package itx.iamservice.core.model;

public final class UserId extends Id {

    public UserId(String id) {
        super(id);
    }

    public static UserId from(String id) {
        return new UserId(id);
    }

}
