package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.ProjectImpl;

import java.util.UUID;

public final class OrganizationBuilder {

    private final ModelBuilder modelBuilder;
    private final Organization organization;

    public OrganizationBuilder(ModelBuilder modelBuilder, Organization organization) {
        this.modelBuilder = modelBuilder;
        this.organization = organization;
    }

    public ProjectBuilder addProject(String name) throws PKIException {
        ProjectId projectId = ProjectId.from(UUID.randomUUID().toString());
        Project project = new ProjectImpl(projectId, name, organization.getId(), organization.getPrivateKey());
        organization.add(project);
        return new ProjectBuilder(this, project);
    }

    public ModelBuilder and() {
        return modelBuilder;
    }

    public Model build() {
        return modelBuilder.build();
    }

}
