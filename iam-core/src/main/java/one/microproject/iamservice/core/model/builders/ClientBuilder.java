package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.caches.ModelCache;

public final class ClientBuilder {

    private final ModelCache modelCache;
    private final ProjectBuilder projectBuilder;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final ClientId clientId;

    public ClientBuilder(ModelCache modelCache, ProjectBuilder projectBuilder, OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        this.modelCache = modelCache;
        this.projectBuilder = projectBuilder;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.clientId = clientId;
    }

    public ClientBuilder addRole(RoleId roleId) {
        modelCache.assignRole(organizationId, projectId, clientId, roleId);
        return this;
    }

    public ProjectBuilder and() {
        return projectBuilder;
    }

    public ModelCache build() {
        return projectBuilder.build();
    }

}
