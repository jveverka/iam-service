package itx.iamservice.persistence.inmemory;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.PersistenceService;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
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
    public void onModelInitialization(ModelWrapper modelWrapper) {
        this.modelWrapper = modelWrapper;
    }

    @Override
    public void onModelChange(Model model) {
        this.modelWrapper = new ModelWrapper(model);
    }

    @Override
    public <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        putData(modelKey, newNode);
    }

    @Override
    public <T> void onNodeUpdated(ModelKey<T> modelKey, T newNode) {
        putData(modelKey, newNode);
    }

    @Override
    public <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode) {
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
    }

    @Override
    public void flush() throws Exception {
        LOG.info("flush: NOOP");
    }

    public String flushToString() throws IOException {
        return mapper.writeValueAsString(modelWrapper);
    }

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
