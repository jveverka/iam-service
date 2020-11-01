package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.caches.ModelCache;

public final class ClientBuilder {

    private final ProjectBuilder projectBuilder;
    private final Client client;

    public ClientBuilder(ProjectBuilder projectBuilder, Client client) {
        this.projectBuilder = projectBuilder;
        this.client = client;
    }

    public ClientBuilder addRole(RoleId roleId) {
        client.addRole(roleId);
        return this;
    }

    public ProjectBuilder and() {
        return projectBuilder;
    }

    public ModelCache build() {
        return projectBuilder.build();
    }

}
