package itx.iamservice.model;

public class ProjectId extends Id {

    public ProjectId(String id) {
        super(id);
    }

    public static ProjectId from(String id) {
        return new ProjectId(id);
    }

}
