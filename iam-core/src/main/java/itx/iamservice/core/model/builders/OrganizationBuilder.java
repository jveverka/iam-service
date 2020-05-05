package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.ProjectImpl;
import itx.iamservice.core.services.caches.ModelCache;

import java.util.UUID;

public final class OrganizationBuilder {

    private final ModelBuilder modelBuilder;
    private final Organization organization;

    public OrganizationBuilder(ModelBuilder modelBuilder, Organization organization) {
        this.modelBuilder = modelBuilder;
        this.organization = organization;
    }

    public ProjectBuilder addProject(String name) throws PKIException {
        ProjectId id = ProjectId.from(UUID.randomUUID().toString());
        return addProject(id, name);
    }

    public ProjectBuilder addProject(ProjectId id, String name) throws PKIException {
        Project project = new ProjectImpl(id, name, organization.getId(), organization.getPrivateKey());
        modelBuilder.addProject(organization.getId(), project);
        return new ProjectBuilder(this, project);
    }

    public ModelBuilder and() {
        return modelBuilder;
    }

    public ModelCache build() {
        return modelBuilder.build();
    }

}
