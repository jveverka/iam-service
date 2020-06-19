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
    public synchronized void onModelInitialization(ModelWrapper modelWrapper) {
        long timeStamp = System.nanoTime();
        this.modelWrapper = modelWrapper;
        flushOnChange();
        LOG.trace("onModelInitialization: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized void onModelChange(Model model) {
        long timeStamp = System.nanoTime();
        this.modelWrapper  = new ModelWrapper(model);
        flushOnChange();
        LOG.trace("onModelChange: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        long timeStamp = System.nanoTime();
        putData(modelKey, newNode);
        flushOnChange();
        LOG.trace("onNodeCreated: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized <T> void onNodeUpdated(ModelKey<T> modelKey, T newNode) {
        long timeStamp = System.nanoTime();
        putData(modelKey, newNode);
        flushOnChange();
        LOG.trace("onNodeUpdated: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
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
        flushOnChange();
        LOG.trace("onNodeDeleted: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized void flush() throws Exception {
        flushToFile();
    }

    public synchronized void flushToFile() throws IOException {
        mapper.writeValue(dataFile.toFile(), modelWrapper);
    }

    public synchronized String flushToString() throws IOException {
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
