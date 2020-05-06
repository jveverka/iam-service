package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.UserImpl;
import itx.iamservice.core.services.caches.ModelCache;

import java.util.UUID;

public final class ProjectBuilder {

    private final ModelCache modelCache;
    private final OrganizationBuilder organizationBuilder;
    private final Project project;

    public ProjectBuilder(ModelCache modelCache, OrganizationBuilder organizationBuilder, Project project) {
        this.modelCache = modelCache;
        this.organizationBuilder = organizationBuilder;
        this.project = project;
    }

    public UserBuilder addUser(String name) throws PKIException {
        UserId id = UserId.from(UUID.randomUUID().toString());
        return addUser(id, name);
    }

    public UserBuilder addUser(UserId id, String name) throws PKIException {
        User user = new UserImpl(id, name, project.getId(), 3600*1000L, 24*3600*1000L, project.getPrivateKey());
        project.add(user);
        return new UserBuilder(this, user);
    }

    public ClientBuilder addClient(String name) {
        ClientId id = ClientId.from(UUID.randomUUID().toString());
        return addClient(id, name);
    }

    public ClientBuilder addClient(ClientId id, String name) {
        String secret = UUID.randomUUID().toString();
        return addClient(id, name, secret);
    }

    public ClientBuilder addClient(ClientId id, String name, String secret) {
        ClientCredentials credentials = new ClientCredentials(id, secret);
        Client client = new Client(credentials, name, 3600*1000L, 24*3600*1000L);
        modelCache.add(organizationBuilder.getOrganization().getId(), project.getId(), client);
        return new ClientBuilder(this, client);
    }

    public ProjectBuilder addRole(Role role)  {
        role.getPermissions().forEach(p ->
            project.addPermission(p)
        );
        modelCache.add(organizationBuilder.getOrganization().getId(), project.getId(), role);
        return this;
    }

    public OrganizationBuilder and() {
        return organizationBuilder;
    }

    public ModelCache build() {
        return organizationBuilder.build();
    }

}
