package itx.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.PersistenceService;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemPersistenceServiceImpl implements PersistenceService {

    private final Path basePath;
    private final ObjectMapper mapper;

    public FileSystemPersistenceServiceImpl(Path basePath) {
        this.basePath = basePath;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }


    private Path createFilePathFromModelId(Path basePath, ModelId id) {
        String fileName = "model-" + id.getId() + ".json";
        return Paths.get(basePath.toString(), fileName);
    }

    @Override
    public void onModelChange(Model model) {

    }

    @Override
    public <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {

    }

    @Override
    public <T> void onNodeUpdated(ModelKey<T> modelKey, T oldNode, T newNode) {

    }

    @Override
    public <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode) {

    }

}
