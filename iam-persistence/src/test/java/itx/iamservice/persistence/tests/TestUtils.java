package itx.iamservice.persistence.tests;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.OrganizationImpl;
import itx.iamservice.core.model.PKIException;

public final class TestUtils {

    public static OrganizationId organizationId = OrganizationId.from("organization-001");

    public static Model createModel() throws PKIException {
        ModelId id = ModelId.from("model-001");
        String name = "model-name";
        Model model = new ModelImpl(id, name);
        Organization organization = new OrganizationImpl(organizationId, "the-org");
        model.add(organization);
        return model;
    }

}
