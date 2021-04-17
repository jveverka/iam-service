package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;
import one.microproject.iamservice.core.services.dto.CreateUserRequest;

import java.util.Optional;
import java.util.UUID;

public final class ProjectBuilder {

    private final ModelCache modelCache;
    private final OrganizationBuilder organizationBuilder;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public ProjectBuilder(ModelCache modelCache, OrganizationBuilder organizationBuilder, OrganizationId organizationId, ProjectId projectId) {
        this.modelCache = modelCache;
        this.organizationBuilder = organizationBuilder;
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    public UserBuilder addUser(String name, String email) throws PKIException {
        UserId id = UserId.from(UUID.randomUUID().toString());
        return addUser(id, name, email, UserProperties.getDefault());
    }

    public UserBuilder addUser(UserId id, String name, String email, UserProperties userProperties) throws PKIException {
        CreateUserRequest request = new CreateUserRequest(id, name, 3600*1000L, 24*3600*1000L, email, userProperties);
        Optional<User> user = modelCache.add(organizationId, projectId, request);
        if (user.isPresent()) {
            return new UserBuilder(modelCache, this, organizationId, projectId, user.get().getId());
        } else {
            throw new UnsupportedOperationException("Create user failed !");
        }
    }

    public ClientBuilder addClient(String name, String redirectURL) {
        ClientId id = ClientId.from(UUID.randomUUID().toString());
        return addClient(id, name, redirectURL);
    }

    public ClientBuilder addClient(ClientId id, String name, String redirectURL) {
        String secret = UUID.randomUUID().toString();
        return addClient(id, name, secret, redirectURL);
    }

    public ClientBuilder addClient(ClientId id, String name, String secret, String redirectURL) {
        return addClient(id, name, secret, ClientProperties.from(redirectURL));
    }

    public ClientBuilder addClient(ClientId id, String name, String secret, ClientProperties properties) {
        CreateClientRequest request = new CreateClientRequest(id, name, 3600*1000L, 24*3600*1000L, secret, properties);
        Optional<Client> client = modelCache.add(organizationId, projectId, request);
        if (client.isPresent()) {
            return new ClientBuilder(modelCache,this, organizationId, projectId, client.get().getId());
        } else {
            throw new UnsupportedOperationException("Create client failed !");
        }
    }

    public ProjectBuilder addPermission(Permission permission) {
        modelCache.addPermission(organizationId, projectId, permission);
        return this;
    }

    public ProjectBuilder addRole(Role role)  {
        role.getPermissions().forEach(p ->
                modelCache.addPermission(organizationId, projectId, p)
        );
        modelCache.add(organizationId, projectId, role);
        return this;
    }

    public OrganizationBuilder and() {
        return organizationBuilder;
    }

    public ModelCache build() {
        return organizationBuilder.build();
    }

}
