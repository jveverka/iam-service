package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.OrganizationImpl;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;

import java.util.UUID;

public final class ModelBuilder {

    private final Model model;

    public ModelBuilder(ModelId id, String name) {
        this.model = new ModelImpl(id, name);
    }

    public ModelBuilder(String name) {
        ModelId id = ModelId.from(UUID.randomUUID().toString());
        this.model = new ModelImpl(id, name);
    }

    public OrganizationBuilder addOrganization(String name) throws PKIException {
        OrganizationId id = OrganizationId.from(UUID.randomUUID().toString());
        return addOrganization(id, name);
    }

    public OrganizationBuilder addOrganization(OrganizationId id, String name) throws PKIException {
        Organization organization = new OrganizationImpl(id, name);
        this.model.add(organization);
        return new OrganizationBuilder(this, organization);
    }

    public void addProject(OrganizationId id, Project project) {
        this.model.add(id, project);
    }

    public Model build() {
        return model;
    }

    public static ModelBuilder builder(String name) {
        return new ModelBuilder(name);
    }

    public static ModelBuilder builder(ModelId id, String name) {
        return new ModelBuilder(id, name);
    }

}
