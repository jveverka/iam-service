package itx.iamservice.core.model;

public class OrganizationId extends Id {

    public OrganizationId(String id) {
        super(id);
    }

    public static OrganizationId from(String id) {
        return new OrganizationId(id);
    }

}
