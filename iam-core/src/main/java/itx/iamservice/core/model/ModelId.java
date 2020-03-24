package itx.iamservice.core.model;

public class ModelId extends Id {

    public ModelId(String id) {
        super(id);
    }

    public static ModelId from(String id) {
        return new ModelId(id);
    }

}
