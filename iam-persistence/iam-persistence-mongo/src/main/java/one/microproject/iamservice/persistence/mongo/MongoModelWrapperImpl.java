package one.microproject.iamservice.persistence.mongo;

import com.mongodb.client.MongoCursor;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ClientWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.OrganizationWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ProjectWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.RoleWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.UserWrapper;
import one.microproject.iamservice.persistence.mongo.wrappers.ClientMongoWrapper;
import one.microproject.iamservice.persistence.mongo.wrappers.ModelInfoWrapper;
import one.microproject.iamservice.persistence.mongo.wrappers.OrganizationMongoWrapper;
import one.microproject.iamservice.persistence.mongo.wrappers.ProjectMongoWrapper;
import one.microproject.iamservice.persistence.mongo.wrappers.RoleMongoWrapper;
import one.microproject.iamservice.persistence.mongo.wrappers.UserMongoWrapper;
import org.mongojack.JacksonMongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static one.microproject.iamservice.persistence.mongo.MongoUtils.convertToId;
import static one.microproject.iamservice.persistence.mongo.MongoUtils.createJacksonMongoCollection;

public class MongoModelWrapperImpl implements ModelWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(MongoModelWrapperImpl.class);

    private final JacksonMongoCollection<ModelInfoWrapper> modelInfoCollection;
    private final JacksonMongoCollection<OrganizationMongoWrapper> organizationCollection;
    private final JacksonMongoCollection<ProjectMongoWrapper> projectCollection;
    private final JacksonMongoCollection<UserMongoWrapper> userCollection;
    private final JacksonMongoCollection<ClientMongoWrapper> clientCollection;
    private final JacksonMongoCollection<RoleMongoWrapper> roleCollection;

    public MongoModelWrapperImpl(MongoConfiguration configuration) {
        modelInfoCollection = createJacksonMongoCollection(configuration, ModelInfoWrapper.class, "modelinfo");
        organizationCollection = createJacksonMongoCollection(configuration, OrganizationMongoWrapper.class, "organizations");
        projectCollection = createJacksonMongoCollection(configuration, ProjectMongoWrapper.class, "projects");
        userCollection = createJacksonMongoCollection(configuration, UserMongoWrapper.class, "users");
        clientCollection = createJacksonMongoCollection(configuration, ClientMongoWrapper.class, "clients");
        roleCollection = createJacksonMongoCollection(configuration, RoleMongoWrapper.class, "roles");
    }

    @Override
    public void onInit(PersistenceService persistenceService, boolean flushOnChange) throws Exception {
        LOG.info("NOOP");
    }

    @Override
    public void flush() throws Exception {
        LOG.info("NOOP");
    }

    @Override
    public boolean isInitialized() {
        ModelInfoWrapper modelInfoWrapper = modelInfoCollection.findOne();
        return modelInfoWrapper != null;
    }

    @Override
    public Model getModel() {
        ModelInfoWrapper modelInfoWrapper = modelInfoCollection.findOne();
        return modelInfoWrapper.getModel();
    }

    @Override
    public void setModel(Model model) {
        modelInfoCollection.insert(new ModelInfoWrapper(model.getId().getId(), model));
    }

    @Override
    public List<OrganizationWrapper> getOrganizations() {
        throw new UnsupportedOperationException("Operation is not implemented.");
    }

    @Override
    public List<ProjectWrapper> getProjects() {
        throw new UnsupportedOperationException("Operation is not implemented.");
    }

    @Override
    public List<UserWrapper> getUsers() {
        throw new UnsupportedOperationException("Operation is not implemented.");
    }

    @Override
    public List<ClientWrapper> getClients() {
        throw new UnsupportedOperationException("Operation is not implemented.");
    }

    @Override
    public List<RoleWrapper> getRoles() {
        throw new UnsupportedOperationException("Operation is not implemented.");
    }

    @Override
    public void putOrganization(ModelKey<Organization> key, Organization value) {
        organizationCollection.insert(new OrganizationMongoWrapper(convertToId(key), key, value));
    }

    @Override
    public void putProject(ModelKey<Project> key, Project value) {
        projectCollection.insert(new ProjectMongoWrapper(convertToId(key), key, value));
    }

    @Override
    public void putUser(ModelKey<User> key, User value) {
        userCollection.insert(new UserMongoWrapper(convertToId(key), key, value));
    }

    @Override
    public void putClient(ModelKey<Client> key, Client value) {
        clientCollection.insert(new ClientMongoWrapper(convertToId(key), key, value));
    }

    @Override
    public void putRole(ModelKey<Role> key, Role value) {
        roleCollection.insert(new RoleMongoWrapper(convertToId(key), key, value));
    }

    @Override
    public Organization removeOrganization(ModelKey<Organization> key) {
        String id = convertToId(key);
        OrganizationMongoWrapper organizationMongoWrapper = organizationCollection.findOneById(id);
        if (organizationMongoWrapper != null) {
            organizationCollection.removeById(id);
            return organizationMongoWrapper.getValue();
        } else {
            return null;
        }
    }

    @Override
    public Project removeProject(ModelKey<Project> key) {
        String id = convertToId(key);
        ProjectMongoWrapper projectMongoWrapper = projectCollection.findOneById(id);
        if (projectMongoWrapper != null) {
            projectCollection.removeById(id);
            return projectMongoWrapper.getValue();
        } else {
            return null;
        }
    }

    @Override
    public User removeUser(ModelKey<User> key) {
        String id = convertToId(key);
        UserMongoWrapper userMongoWrapper = userCollection.findOneById(id);
        if (userMongoWrapper != null) {
            userCollection.removeById(id);
            return userMongoWrapper.getValue();
        } else {
            return null;
        }
    }

    @Override
    public Client removeClient(ModelKey<Client> key) {
        String id = convertToId(key);
        ClientMongoWrapper clientMongoWrapper = clientCollection.findOneById(id);
        if (clientMongoWrapper != null) {
            clientCollection.removeById(id);
            return clientMongoWrapper.getValue();
        } else {
            return null;
        }
    }

    @Override
    public Role removeRole(ModelKey<Role> key) {
        String id = convertToId(key);
        RoleMongoWrapper roleMongoWrapper = roleCollection.findOneById(id);
        if (roleMongoWrapper != null) {
            roleCollection.removeById(id);
            return roleMongoWrapper.getValue();
        } else {
            return null;
        }
    }

    @Override
    public Organization getOrganization(ModelKey<Organization> key) {
        OrganizationMongoWrapper organizationMongoWrapper = organizationCollection.findOneById(convertToId(key));
        if (organizationMongoWrapper == null) {
            return null;
        } else {
            return organizationMongoWrapper.getValue();
        }
    }

    @Override
    public Project getProject(ModelKey<Project> key) {
        ProjectMongoWrapper projectMongoWrapper = projectCollection.findOneById(convertToId(key));
        if (projectMongoWrapper == null) {
            return null;
        } else {
            return projectMongoWrapper.getValue();
        }
    }

    @Override
    public User getUser(ModelKey<User> key) {
        UserMongoWrapper userMongoWrapper = userCollection.findOneById(convertToId(key));
        if (userMongoWrapper == null) {
            return null;
        } else {
            return userMongoWrapper.getValue();
        }
    }

    @Override
    public Client getClient(ModelKey<Client> key) {
        ClientMongoWrapper clientMongoWrapper = clientCollection.findOneById(convertToId(key));
        if (clientMongoWrapper == null) {
            return null;
        } else {
            return clientMongoWrapper.getValue();
        }
    }

    @Override
    public Role getRole(ModelKey<Role> key) {
        RoleMongoWrapper roleMongoWrapper = roleCollection.findOneById(convertToId(key));
        if (roleMongoWrapper == null) {
            return null;
        } else {
            return roleMongoWrapper.getValue();
        }
    }

    @Override
    public Set<ModelKey<Organization>> getOrganizationsKeys() {
        Set<ModelKey<Organization>> organizationKeys = new HashSet<>();
        MongoCursor<OrganizationMongoWrapper> organizationsIterator = organizationCollection.getMongoCollection().find().iterator();
        while (organizationsIterator.hasNext()) {
            organizationKeys.add(organizationsIterator.next().getKey());
        }
        return organizationKeys;
    }

    @Override
    public Set<ModelKey<Project>> getProjectKeys() {
        Set<ModelKey<Project>> projectKeys = new HashSet<>();
        MongoCursor<ProjectMongoWrapper> projectIterator = projectCollection.getMongoCollection().find().iterator();
        while (projectIterator.hasNext()) {
            projectKeys.add(projectIterator.next().getKey());
        }
        return projectKeys;
    }

    @Override
    public Set<ModelKey<User>> getUserKeys() {
        Set<ModelKey<User>> userKeys = new HashSet<>();
        MongoCursor<UserMongoWrapper> userIterator = userCollection.getMongoCollection().find().iterator();
        while (userIterator.hasNext()) {
            userKeys.add(userIterator.next().getKey());
        }
        return userKeys;
    }

    @Override
    public Set<ModelKey<Client>> getClientKeys() {
        Set<ModelKey<Client>> clientKeys = new HashSet<>();
        MongoCursor<ClientMongoWrapper> clientIterator = clientCollection.getMongoCollection().find().iterator();
        while (clientIterator.hasNext()) {
            clientKeys.add(clientIterator.next().getKey());
        }
        return clientKeys;
    }

    @Override
    public Set<ModelKey<Role>> getRoleKeys() {
        Set<ModelKey<Role>> roleKeys = new HashSet<>();
        MongoCursor<RoleMongoWrapper> roleIterator = roleCollection.getMongoCollection().find().iterator();
        while (roleIterator.hasNext()) {
            roleKeys.add(roleIterator.next().getKey());
        }
        return roleKeys;
    }

    @Override
    public Collection<Organization> getAllOrganizations() {
        List<Organization> organizations = new ArrayList<>();
        MongoCursor<OrganizationMongoWrapper> organizationsIterator = organizationCollection.getMongoCollection().find().iterator();
        while (organizationsIterator.hasNext()) {
            organizations.add(organizationsIterator.next().getValue());
        }
        return organizations;
    }

    @Override
    public Set<Map.Entry<ModelKey<Client>, Client>> getClientEntrySet() {
        Map<ModelKey<Client>, Client> clients = new HashMap<>();
        MongoCursor<ClientMongoWrapper> clientsIterator = clientCollection.getMongoCollection().find().iterator();
        while (clientsIterator.hasNext()) {
            ClientMongoWrapper c = clientsIterator.next();
            clients.put(c.getKey(), c.getValue());
        }
        return clients.entrySet();
    }

    @Override
    public Set<Map.Entry<ModelKey<User>, User>> getUserEntrySet() {
        Map<ModelKey<User>, User> users = new HashMap<>();
        MongoCursor<UserMongoWrapper> usersIterator = userCollection.getMongoCollection().find().iterator();
        while (usersIterator.hasNext()) {
            UserMongoWrapper u = usersIterator.next();
            users.put(u.getKey(), u.getValue());
        }
        return users.entrySet();
    }

    @Override
    public Set<Map.Entry<ModelKey<Role>, Role>> getRoleEntrySet() {
        Map<ModelKey<Role>, Role> roles = new HashMap<>();
        MongoCursor<RoleMongoWrapper> rolesIterator = roleCollection.getMongoCollection().find().iterator();
        while (rolesIterator.hasNext()) {
            RoleMongoWrapper r = rolesIterator.next();
            roles.put(r.getKey(), r.getValue());
        }
        return roles.entrySet();
    }

    @Override
    public void setPersistenceService(PersistenceService persistenceService) {
        LOG.info("NOOP");
    }

}
