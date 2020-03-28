package itx.iamservice.core.services.impl.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.services.persistence.PersistenceResult;
import itx.iamservice.core.services.persistence.PersistenceService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * In-memory persistence service implementation (test-purposes) mainly.
 */
public class InMemoryPersistenceServiceImpl implements PersistenceService {

    private final ObjectMapper mapper;
    private final Map<ModelId, String> serializedModels;

    public InMemoryPersistenceServiceImpl() {
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        this.serializedModels = new ConcurrentHashMap<>();
    }

    @Override
    public Future<PersistenceResult> saveModel(Model model) {
        CompletableFuture<PersistenceResult> result = new CompletableFuture<>();
        try {
            serializedModels.put(model.getId(), mapper.writeValueAsString(model));
            result.complete(PersistenceResult.ok());
        } catch (JsonProcessingException e) {
            result.completeExceptionally(e);
        }
        return result;
    }

    @Override
    public Future<Model> loadModel(ModelId id) {
        CompletableFuture<Model> result = new CompletableFuture<>();
        try {
            Model model = mapper.readValue(serializedModels.get(id), ModelImpl.class);
            result.complete(model);
        } catch (IOException e) {
            result.completeExceptionally(e);
        }
        return result;
    }

    public String getSerialized(ModelId id) {
        return serializedModels.get(id);
    }

}
