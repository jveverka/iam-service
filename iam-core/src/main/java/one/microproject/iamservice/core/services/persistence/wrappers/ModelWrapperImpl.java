package one.microproject.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModelWrapperImpl implements ModelWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(ModelWrapperImpl.class);

    private Model model;
    private final Map<ModelKey<Organization>, Organization> organizations;
    private final Map<ModelKey<Project>, Project> projects;
    private final Map<ModelKey<User>, User> users;
    private final Map<ModelKey<Client>, Client> clients;
    private final Map<ModelKey<Role>, Role> roles;

    private PersistenceService persistenceService;
    private boolean flushOnChange = false;

    public ModelWrapperImpl(Model model, PersistenceService persistenceService, boolean flushOnChange) {
        this.model = model;
        this.organizations  = new ConcurrentHashMap<>();
        this.projects  = new ConcurrentHashMap<>();
        this.users  = new ConcurrentHashMap<>();
        this.clients  = new ConcurrentHashMap<>();
        this.roles  = new ConcurrentHashMap<>();
        this.persistenceService = persistenceService;
        this.flushOnChange = flushOnChange;
    }

    @JsonCreator
    public ModelWrapperImpl(@JsonProperty("model") Model model,
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

    @Override
    public void onInit(PersistenceService persistenceService, boolean flushOnChange) throws Exception {
        LOG.info("onInit: flushOnChange={}", flushOnChange);
        LOG.info("onInit: organizations={}", organizations.size());
        LOG.info("onInit: projects={}", projects.size());
        LOG.info("onInit: users={}", users.size());
        LOG.info("onInit: clients={}", clients.size());
        LOG.info("onInit: roles={}", roles.size());
        this.persistenceService = persistenceService;
        this.flushOnChange = flushOnChange;
        flushOnChange();
    }

    @Override
    public void flush() throws Exception {
        persistenceService.onModelChange(this);
    }

    private void flushOnChange() {
        if (flushOnChange) {
            try {
                persistenceService.onModelChange(this);
            } catch (IOException e) {
                LOG.error("Persistence Error: {}", e.getMessage());
            }
        }
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public List<OrganizationWrapper> getOrganizations() {
        List<OrganizationWrapper> result = new ArrayList<>();
        organizations.forEach((k,v) -> result.add(new OrganizationWrapper(k,v)));
        return result;
    }

    @Override
    public List<ProjectWrapper> getProjects() {
        List<ProjectWrapper> result = new ArrayList<>();
        projects.forEach((k,v) -> result.add(new ProjectWrapper(k,v)));
        return result;
    }

    @Override
    public List<UserWrapper> getUsers() {
        List<UserWrapper> result = new ArrayList<>();
        users.forEach((k,v) -> result.add(new UserWrapper(k,v)));
        return result;
    }

    @Override
    public List<ClientWrapper> getClients() {
        List<ClientWrapper> result = new ArrayList<>();
        clients.forEach((k,v) -> result.add(new ClientWrapper(k,v)));
        return result;
    }

    @Override
    public List<RoleWrapper> getRoles() {
        List<RoleWrapper> result = new ArrayList<>();
        roles.forEach((k,v) -> result.add(new RoleWrapper(k,v)));
        return result;
    }

    @JsonIgnore
    @Override
    public void putOrganization(ModelKey<Organization> key, Organization value) {
        organizations.put(key, value);
        flushOnChange();
    }

    @JsonIgnore
    @Override
    public void putProject(ModelKey<Project> key, Project value) {
        projects.put(key, value);
        flushOnChange();
    }

    @JsonIgnore
    @Override
    public void putUser(ModelKey<User> key, User value) {
        users.put(key, value);
        flushOnChange();
    }

    @JsonIgnore
    @Override
    public void putClient(ModelKey<Client> key, Client value) {
        clients.put(key, value);
        flushOnChange();
    }

    @JsonIgnore
    @Override
    public void putRole(ModelKey<Role> key, Role value) {
        roles.put(key, value);
        flushOnChange();
    }

    @JsonIgnore
    @Override
    public Organization removeOrganization(ModelKey<Organization> key) {
        Organization organization = organizations.remove(key);
        if (organization !=  null) {
            flushOnChange();
        }
        return organization;
    }

    @JsonIgnore
    @Override
    public Project removeProject(ModelKey<Project> key) {
        Project project = projects.remove(key);
        if (project !=  null) {
            flushOnChange();
        }
        return project;
    }

    @JsonIgnore
    @Override
    public User removeUser(ModelKey<User> key) {
        User user = users.remove(key);
        if (user !=  null) {
            flushOnChange();
        }
        return user;
    }

    @JsonIgnore
    @Override
    public Client removeClient(ModelKey<Client> key) {
        Client client = clients.remove(key);
        if (client !=  null) {
            flushOnChange();
        }
        return client;
    }

    @JsonIgnore
    @Override
    public Role removeRole(ModelKey<Role> key) {
        Role role = roles.remove(key);
        if (role !=  null) {
            flushOnChange();
        }
        return role;
    }

    @JsonIgnore
    @Override
    public Organization getOrganization(ModelKey<Organization> key) {
        return organizations.get(key);
    }

    @JsonIgnore
    @Override
    public Project getProject(ModelKey<Project> key) {
        return projects.get(key);
    }

    @JsonIgnore
    @Override
    public User getUser(ModelKey<User> key) {
        return users.get(key);
    }

    @JsonIgnore
    @Override
    public Client getClient(ModelKey<Client> key) {
        return clients.get(key);
    }

    @JsonIgnore
    @Override
    public Role getRole(ModelKey<Role> key) {
        return roles.get(key);
    }

    @JsonIgnore
    @Override
    public Set<ModelKey<Organization>> getOrganizationsKeys() {
        return organizations.keySet();
    }

    @JsonIgnore
    @Override
    public Set<ModelKey<Project>> getProjectKeys() {
        return projects.keySet();
    }

    @JsonIgnore
    @Override
    public Set<ModelKey<User>> getUserKeys() {
        return users.keySet();
    }

    @JsonIgnore
    @Override
    public Set<ModelKey<Client>> getClientKeys() {
        return clients.keySet();
    }

    @JsonIgnore
    @Override
    public Set<ModelKey<Role>> getRoleKeys() {
        return roles.keySet();
    }

    @JsonIgnore
    @Override
    public Collection<Organization> getAllOrganizations() {
        return organizations.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @JsonIgnore
    @Override
    public Set<Map.Entry<ModelKey<Client>, Client>> getClientEntrySet() {
        return clients.entrySet();
    }

    @JsonIgnore
    @Override
    public Set<Map.Entry<ModelKey<User>, User>> getUserEntrySet() {
        return users.entrySet();
    }

    @JsonIgnore
    @Override
    public Set<Map.Entry<ModelKey<Role>, Role>> getRoleEntrySet() {
        return roles.entrySet();
    }

    @JsonIgnore
    @Override
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
