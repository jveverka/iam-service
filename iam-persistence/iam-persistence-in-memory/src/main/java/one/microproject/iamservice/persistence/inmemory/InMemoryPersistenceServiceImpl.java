package one.microproject.iamservice.persistence.inmemory;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InMemoryPersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryPersistenceServiceImpl.class);

    private ModelWrapper modelWrapper;
    private final ObjectMapper mapper;

    public InMemoryPersistenceServiceImpl() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public synchronized void onModelInitialization(ModelWrapper modelWrapper) {
        long timeStamp = System.nanoTime();
        this.modelWrapper = modelWrapper;
        LOG.info("onModelInitialization: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized void onModelChange(Model model) {
        long timeStamp = System.nanoTime();
        this.modelWrapper = new ModelWrapper(model);
        LOG.info("onModelChange: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        long timeStamp = System.nanoTime();
        putData(modelKey, newNode);
        LOG.info("onNodeCreated: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized <T> void onNodeUpdated(ModelKey<T> modelKey, T newNode) {
        long timeStamp = System.nanoTime();
        putData(modelKey, newNode);
        LOG.info("onNodeUpdated: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode) {
        long timeStamp = System.nanoTime();
        if (Organization.class.equals(modelKey.getType())) {
            modelWrapper.removeOrganization((ModelKey<Organization>)modelKey);
        } else if (Project.class.equals(modelKey.getType())) {
            modelWrapper.removeProject((ModelKey<Project>)modelKey);
        } else if (Client.class.equals(modelKey.getType())) {
            modelWrapper.removeClient((ModelKey<Client>)modelKey);
        } else if (User.class.equals(modelKey.getType())) {
            modelWrapper.removeUser((ModelKey<User>)modelKey);
        } else if (Role.class.equals(modelKey.getType())) {
            modelWrapper.removeRole((ModelKey<Role>)modelKey);
        }
        LOG.info("onNodeDeleted: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized void flush() throws Exception {
        LOG.info("flush: NOOP");
    }

    public synchronized String flushToString() throws IOException {
        return mapper.writeValueAsString(modelWrapper);
    }

    @SuppressWarnings("unchecked")
    private <T> void putData(ModelKey<T> modelKey, T newNode) {
        if (Organization.class.equals(modelKey.getType())) {
            modelWrapper.putOrganization((ModelKey<Organization>)modelKey, (Organization)newNode);
        } else if (Project.class.equals(modelKey.getType())) {
            modelWrapper.putProject((ModelKey<Project>)modelKey, (Project)newNode);
        } else if (Client.class.equals(modelKey.getType())) {
            modelWrapper.putClient((ModelKey<Client>)modelKey, (Client)newNode);
        } else if (User.class.equals(modelKey.getType())) {
            modelWrapper.putUser((ModelKey<User>)modelKey, (User)newNode);
        } else if (Role.class.equals(modelKey.getType())) {
            modelWrapper.putRole((ModelKey<Role>)modelKey, (Role)newNode);
        }
    }

}
