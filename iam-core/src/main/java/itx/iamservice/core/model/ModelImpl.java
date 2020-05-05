package itx.iamservice.core.model;

public class ModelImpl implements Model {

    private final ModelId id;
    private final String name;

    public ModelImpl(ModelId id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ModelId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}
