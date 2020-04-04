package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.RoleId;

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

    public Model build() {
        return projectBuilder.build();
    }

}