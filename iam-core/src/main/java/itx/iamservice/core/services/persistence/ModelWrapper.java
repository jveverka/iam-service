package itx.iamservice.core.services.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.keys.ModelKey;

import java.util.Map;

public class ModelWrapper {

    private final Model model;
    private final Map<ModelKey<Organization>, Organization> organizations;
    private final Map<ModelKey<Project>, Project> projects;
    private final Map<ModelKey<User>, User> users;
    private final Map<ModelKey<Client>, Client> clients;
    private final Map<ModelKey<Role>, Role> roles;

    @JsonCreator
    public ModelWrapper(@JsonProperty("model") Model model,
                        @JsonProperty("organizations") Map<ModelKey<Organization>, Organization> organizations,
                        @JsonProperty("projects") Map<ModelKey<Project>, Project> projects,
                        @JsonProperty("users") Map<ModelKey<User>, User> users,
                        @JsonProperty("clients") Map<ModelKey<Client>, Client> clients,
                        @JsonProperty("roles") Map<ModelKey<Role>, Role> roles) {
        this.model = model;
        this.organizations = organizations;
        this.projects = projects;
        this.users = users;
        this.clients = clients;
        this.roles = roles;
    }

    public Model getModel() {
        return model;
    }

    public Map<ModelKey<Organization>, Organization> getOrganizations() {
        return organizations;
    }

    public Map<ModelKey<Project>, Project> getProjects() {
        return projects;
    }

    public Map<ModelKey<User>, User> getUsers() {
        return users;
    }

    public Map<ModelKey<Client>, Client> getClients() {
        return clients;
    }

    public Map<ModelKey<Role>, Role> getRoles() {
        return roles;
    }

}
