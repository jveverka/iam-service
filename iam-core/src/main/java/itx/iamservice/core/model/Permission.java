package itx.iamservice.core.model;

public class Permission {

    private final String id;
    private final String service;
    private final String resource;
    private final String action;

    public Permission(String service, String resource, String action) {
        this.id = service + "." + resource + "." + action;
        this.service = service;
        this.resource = resource;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }

}
