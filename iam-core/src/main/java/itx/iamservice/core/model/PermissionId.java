package itx.iamservice.core.model;

public class PermissionId extends Id {

    public PermissionId(String id) {
        super(id);
    }

    public static PermissionId from(String id) {
        return new PermissionId(id);
    }

}
