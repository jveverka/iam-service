package itx.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
import itx.iamservice.core.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemPersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemPersistenceServiceImpl.class);

    private final Path dataFile;
    private final ObjectMapper mapper;
    private final boolean flushOnChange;

    private ModelWrapper modelWrapper;

    public FileSystemPersistenceServiceImpl(Path dataFile, boolean flushOnChange) {
        this.dataFile = dataFile;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.flushOnChange = flushOnChange;
        LOG.info("FileSystemPersistence: dataFile={}, flushOnChange={}", dataFile, flushOnChange);
    }

    @Override
    public void onModelInitialization(ModelWrapper modelWrapper) {
        this.modelWrapper = modelWrapper;
        flushOnChange();
    }

    @Override
    public void onModelChange(Model model) {
        this.modelWrapper  = new ModelWrapper(model);
        flushOnChange();
    }

    @Override
    public <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        putData(modelKey, newNode);
        flushOnChange();
    }

    @Override
    public <T> void onNodeUpdated(ModelKey<T> modelKey, T newNode) {
        putData(modelKey, newNode);
        flushOnChange();
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
        flushOnChange();
    }

    @Override
    public void flush() throws Exception {
        flushToFile();
    }

    public void flushToFile() throws IOException {
        mapper.writeValue(dataFile.toFile(), modelWrapper);
    }

    public String flushToString() throws IOException {
        return mapper.writeValueAsString(modelWrapper);
    }

    private void flushOnChange() {
        try {
            if (flushOnChange) {
                flush();
            }
        } catch (Exception e) {
            LOG.error("Error: ", e);
        }
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
