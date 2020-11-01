package one.microproject.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemDataLoadServiceImpl implements DataLoadService {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemDataLoadServiceImpl.class);

    private final PersistenceService persistenceService;
    private Path dataFile;
    private String serializedModel;

    public FileSystemDataLoadServiceImpl(Path dataFile, PersistenceService persistenceService) {
        LOG.info("FileSystemPersistence: loading from dataFile={}", dataFile);
        this.persistenceService = persistenceService;
        this.dataFile = dataFile;
        this.serializedModel = null;
    }

    public FileSystemDataLoadServiceImpl(String serializedModel, PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        this.dataFile = null;
        this.serializedModel = serializedModel;
    }

    @Override
    public ModelCache populateCache() throws IOException {
        long timeStamp = System.nanoTime();
        ObjectMapper mapper = new ObjectMapper();
        if (dataFile != null) {
            ModelWrapper modelWrapper = mapper.readValue(dataFile.toFile(), ModelWrapper.class);
            LOG.trace("populateCache: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
            return new ModelCacheImpl(modelWrapper, persistenceService);
        } else if (serializedModel != null) {
            ModelWrapper modelWrapper = mapper.readValue(serializedModel, ModelWrapper.class);
            LOG.trace("populateCache: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
            return new ModelCacheImpl(modelWrapper, persistenceService);
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
