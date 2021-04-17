package one.microproject.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.services.persistence.PersistenceService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModelWrapper {

    @JsonIgnore
    void onInit(PersistenceService persistenceService, boolean flushOnChange) throws Exception;

    @JsonIgnore
    void flush() throws Exception;

    @JsonIgnore
    boolean isInitialized();

    Model getModel();

    void setModel(Model model);

    List<OrganizationWrapper> getOrganizations();

    List<ProjectWrapper> getProjects();

    List<UserWrapper> getUsers();

    List<ClientWrapper> getClients();

    List<RoleWrapper> getRoles();

    @JsonIgnore
    void putOrganization(ModelKey<Organization> key, Organization value);

    @JsonIgnore
    void putProject(ModelKey<Project> key, Project value);

    @JsonIgnore
    void putUser(ModelKey<User> key, User value);

    @JsonIgnore
    void putClient(ModelKey<Client> key, Client value);

    @JsonIgnore
    void putRole(ModelKey<Role> key, Role value);

    @JsonIgnore
    Organization removeOrganization(ModelKey<Organization> key);

    @JsonIgnore
    Project removeProject(ModelKey<Project> key);

    @JsonIgnore
    User removeUser(ModelKey<User> key);

    @JsonIgnore
    Client removeClient(ModelKey<Client> key);

    @JsonIgnore
    Role removeRole(ModelKey<Role> key);

    @JsonIgnore
    Organization getOrganization(ModelKey<Organization> key);

    @JsonIgnore
    Project getProject(ModelKey<Project> key);

    @JsonIgnore
    User getUser(ModelKey<User> key);

    @JsonIgnore
    Client getClient(ModelKey<Client> key);

    @JsonIgnore
    Role getRole(ModelKey<Role> key);

    @JsonIgnore
    Set<ModelKey<Organization>> getOrganizationsKeys();

    @JsonIgnore
    Set<ModelKey<Project>> getProjectKeys();

    @JsonIgnore
    Set<ModelKey<User>> getUserKeys();

    @JsonIgnore
    Set<ModelKey<Client>> getClientKeys();

    @JsonIgnore
    Set<ModelKey<Role>> getRoleKeys();

    @JsonIgnore
    Collection<Organization> getAllOrganizations();

    @JsonIgnore
    Set<Map.Entry<ModelKey<Client>, Client>> getClientEntrySet();

    @JsonIgnore
    Set<Map.Entry<ModelKey<User>, User>> getUserEntrySet();

    @JsonIgnore
    Set<Map.Entry<ModelKey<Role>, Role>> getRoleEntrySet();

    @JsonIgnore
    void setPersistenceService(PersistenceService persistenceService);

}
