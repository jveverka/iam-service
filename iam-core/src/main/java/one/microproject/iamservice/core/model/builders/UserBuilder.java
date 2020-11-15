package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPCredentials;
import one.microproject.iamservice.core.services.caches.ModelCache;

public final class UserBuilder {

    private final ModelCache modelCache;
    private final ProjectBuilder projectBuilder;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final UserId userId;

    public UserBuilder(ModelCache modelCache, ProjectBuilder projectBuilder, OrganizationId organizationId, ProjectId projectId, UserId userId) {
        this.modelCache = modelCache;
        this.projectBuilder = projectBuilder;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.userId = userId;
    }

    public UserBuilder addRole(RoleId roleId) {
        modelCache.assignRole(organizationId, projectId, userId, roleId);
        return this;
    }

    public UserBuilder addUserNamePasswordCredentials(UserId userId, String password) throws PKIException {
        UPCredentials upCredentials = new UPCredentials(userId, password);
        modelCache.setCredentials(organizationId, projectId, userId, upCredentials);
        return this;
    }

    public UserBuilder addUserNamePasswordCredentials(String userName, String password) throws PKIException {
        UserId userId = UserId.from(userName);
        UPCredentials upCredentials = new UPCredentials(userId, password);
        modelCache.setCredentials(organizationId, projectId, userId, upCredentials);
        return this;
    }

    public ProjectBuilder and() {
        return projectBuilder;
    }

    public ModelCache build() {
        return projectBuilder.build();
    }

}
