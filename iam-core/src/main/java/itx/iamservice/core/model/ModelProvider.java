package itx.iamservice.core.model;

@Deprecated
public class ModelProvider {

    private static Model model;

    public static void setModel(Model model) {
        ModelProvider.model = model;
    }

    public static Model getModel() {
        return model;
    }

}
