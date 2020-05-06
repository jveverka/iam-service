package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.ProjectImpl;
import itx.iamservice.core.services.caches.ModelCache;

import java.util.UUID;

public final class OrganizationBuilder {

    private final ModelCache modelCache;
    private final ModelBuilder modelBuilder;
    private final Organization organization;

    public OrganizationBuilder(ModelCache modelCache, ModelBuilder modelBuilder, Organization organization) {
        this.modelCache = modelCache;
        this.modelBuilder = modelBuilder;
        this.organization = organization;
    }

    public ProjectBuilder addProject(String name) throws PKIException {
        ProjectId id = ProjectId.from(UUID.randomUUID().toString());
        return addProject(id, name);
    }

    public ProjectBuilder addProject(ProjectId id, String name) throws PKIException {
        Project project = new ProjectImpl(id, name, organization.getId(), organization.getPrivateKey());
        modelCache.add(organization.getId(), project);
        return new ProjectBuilder(modelCache,this, project);
    }

    protected Organization getOrganization() {
        return organization;
    }

    public ModelBuilder and() {
        return modelBuilder;
    }

    public ModelCache build() {
        return modelBuilder.build();
    }

}
