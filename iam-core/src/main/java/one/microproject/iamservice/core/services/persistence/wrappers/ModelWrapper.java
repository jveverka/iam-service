package one.microproject.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.keys.ModelKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelWrapper {

    private final Model model;
    private final Map<ModelKey<Organization>, Organization> organizations;
    private final Map<ModelKey<Project>, Project> projects;
    private final Map<ModelKey<User>, User> users;
    private final Map<ModelKey<Client>, Client> clients;
    private final Map<ModelKey<Role>, Role> roles;

    public ModelWrapper(Model model) {
        this.model = model;
        this.organizations  = new ConcurrentHashMap<>();
        this.projects  = new ConcurrentHashMap<>();
        this.users  = new ConcurrentHashMap<>();
        this.clients  = new ConcurrentHashMap<>();
        this.roles  = new ConcurrentHashMap<>();
    }

    @JsonCreator
    public ModelWrapper(@JsonProperty("model") Model model,
                        @JsonProperty("organizations") List<OrganizationWrapper> organizations,
                        @JsonProperty("projects") List<ProjectWrapper> projects,
                        @JsonProperty("users") List<UserWrapper> users,
                        @JsonProperty("clients") List<ClientWrapper> clients,
                        @JsonProperty("roles") List<RoleWrapper> roles) {
        this.model = model;
        this.organizations  = new ConcurrentHashMap<>();
        this.projects  = new ConcurrentHashMap<>();
        this.users  = new ConcurrentHashMap<>();
        this.clients  = new ConcurrentHashMap<>();
        this.roles  = new ConcurrentHashMap<>();
        organizations.forEach(o -> this.organizations.put(o.getKey(), o.getValue()));
        projects.forEach(p -> this.projects.put(p.getKey(), p.getValue()));
        users.forEach(u -> this.users.put(u.getKey(), u.getValue()));
        clients.forEach(c -> this.clients.put(c.getKey(), c.getValue()));
        roles.forEach(r -> this.roles.put(r.getKey(), r.getValue()));
    }

    public Model getModel() {
        return model;
    }

    public List<OrganizationWrapper> getOrganizations() {
        List<OrganizationWrapper> result = new ArrayList<>();
        organizations.forEach((k,v) -> result.add(new OrganizationWrapper(k,v)));
        return result;
    }

    public List<ProjectWrapper> getProjects() {
        List<ProjectWrapper> result = new ArrayList<>();
        projects.forEach((k,v) -> result.add(new ProjectWrapper(k,v)));
        return result;
    }

    public List<UserWrapper> getUsers() {
        List<UserWrapper> result = new ArrayList<>();
        users.forEach((k,v) -> result.add(new UserWrapper(k,v)));
        return result;
    }

    public List<ClientWrapper> getClients() {
        List<ClientWrapper> result = new ArrayList<>();
        clients.forEach((k,v) -> result.add(new ClientWrapper(k,v)));
        return result;
    }

    public List<RoleWrapper> getRoles() {
        List<RoleWrapper> result = new ArrayList<>();
        roles.forEach((k,v) -> result.add(new RoleWrapper(k,v)));
        return result;
    }

    public void putOrganization(ModelKey<Organization> key, Organization value) {
        organizations.put(key, value);
    }

    public void putProject(ModelKey<Project> key, Project value) {
        projects.put(key, value);
    }

    public void putUser(ModelKey<User> key, User value) {
        users.put(key, value);
    }

    public void putClient(ModelKey<Client> key, Client value) {
        clients.put(key, value);
    }

    public void putRole(ModelKey<Role> key, Role value) {
        roles.put(key, value);
    }

    public void removeOrganization(ModelKey<Organization> key) {
        organizations.remove(key);
    }

    public void removeProject(ModelKey<Project> key) {
        projects.remove(key);
    }

    public void removeUser(ModelKey<User> key) {
        users.remove(key);
    }

    public void removeClient(ModelKey<Client> key) {
        clients.remove(key);
    }

    public void removeRole(ModelKey<Role> key) {
        roles.remove(key);
    }

}
