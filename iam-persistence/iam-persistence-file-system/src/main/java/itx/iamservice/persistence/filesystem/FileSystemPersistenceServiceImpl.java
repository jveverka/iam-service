package itx.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.services.persistence.PersistenceResult;
import itx.iamservice.core.services.persistence.PersistenceService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class FileSystemPersistenceServiceImpl implements PersistenceService {

    private final Path basePath;
    private final ObjectMapper mapper;

    public FileSystemPersistenceServiceImpl(Path basePath) {
        this.basePath = basePath;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public Future<PersistenceResult> saveModel(Model model) {
        CompletableFuture<PersistenceResult> result = new CompletableFuture<>();
        try {
            File file = new File(createFilePathFromModelId(basePath, model.getId()).toString());
            mapper.writeValue(file, model);
            result.complete(PersistenceResult.ok());
            return result;
        } catch (Exception e) {
            result.completeExceptionally(e);
            return result;
        }
    }

    @Override
    public Future<Model> loadModel(ModelId id) {
        CompletableFuture<Model> result = new CompletableFuture<>();
        try {
            File file = new File(createFilePathFromModelId(basePath, id).toString());
            Model model = mapper.readValue(file, Model.class);
            result.complete(model);
            return result;
        } catch (Exception e) {
            result.completeExceptionally(e);
            return result;
        }
    }

    private Path createFilePathFromModelId(Path basePath, ModelId id) {
        String fileName = "model-" + id.getId() + ".json";
        return Paths.get(basePath.toString(), fileName);
    }

}
