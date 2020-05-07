package itx.iamservice.persistence.filesystem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.impl.caches.ModelCacheImpl;
import itx.iamservice.core.services.persistence.DataLoadService;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
import itx.iamservice.core.services.persistence.PersistenceService;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemDataLoadServiceImpl implements DataLoadService {

    private final ModelWrapper modelWrapper;
    private final PersistenceService persistenceService;

    public FileSystemDataLoadServiceImpl(Path dataFile, PersistenceService persistenceService) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.modelWrapper = mapper.readValue(dataFile.toFile(), ModelWrapper.class);
        this.persistenceService = persistenceService;
    }

    public FileSystemDataLoadServiceImpl(String serializedModel, PersistenceService persistenceService) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.modelWrapper = mapper.readValue(serializedModel, ModelWrapper.class);
        this.persistenceService = persistenceService;
    }

    @Override
    public ModelCache populateCache() {
        return new ModelCacheImpl(modelWrapper, persistenceService);
    }

}
