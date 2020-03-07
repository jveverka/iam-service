package itx.iamservice.model;

public class RoleId extends Id {

    public RoleId(String id) {
        super(id);
    }

    public static RoleId from(String id) {
        return new RoleId(id);
    }

}
