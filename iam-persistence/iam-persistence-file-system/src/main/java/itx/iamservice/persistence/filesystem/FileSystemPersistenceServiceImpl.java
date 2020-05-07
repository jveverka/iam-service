package itx.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
import itx.iamservice.core.services.persistence.PersistenceService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemPersistenceServiceImpl implements PersistenceService {

    private final Path basePath;
    private final ObjectMapper mapper;

    private ModelWrapper modelWrapper;

    public FileSystemPersistenceServiceImpl(Path basePath) {
        this.basePath = basePath;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }


    private Path createFilePathFromModelId(Path basePath, ModelId id) {
        String fileName = "model-" + id.getId() + ".json";
        return Paths.get(basePath.toString(), fileName);
    }

    @Override
    public void onModelInitialization(ModelWrapper modelWrapper) {
        this.modelWrapper = modelWrapper;
    }

    @Override
    public void onModelChange(Model model) {
        this.modelWrapper  = new ModelWrapper(model);
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
        flushToFile();
    }

    public void flushToFile() throws IOException {
        Path filePath = createFilePathFromModelId(basePath, modelWrapper.getModel().getId());
        mapper.writeValue(filePath.toFile(), modelWrapper);
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
