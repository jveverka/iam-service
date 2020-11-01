package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateProjectRequest;

import java.util.Collection;
import java.util.Optional;
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

    public ProjectBuilder addProject(String name, Collection<String> audience) throws PKIException {
        ProjectId id = ProjectId.from(UUID.randomUUID().toString());
        return addProject(id, name, audience);
    }

    public ProjectBuilder addProject(ProjectId id, String name, Collection<String> audience) throws PKIException {
        CreateProjectRequest request = new CreateProjectRequest(id, name, audience);
        Optional<Project> project = modelCache.add(organization.getId(), request);
        if (project.isPresent()) {
            return new ProjectBuilder(modelCache, this, project.get());
        } else {
            throw new UnsupportedOperationException("Create project failed !");
        }
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
