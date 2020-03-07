package itx.iamservice.core.services.dto;

public class CreateOrganizationRequest {

    private final String name;

    public CreateOrganizationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
