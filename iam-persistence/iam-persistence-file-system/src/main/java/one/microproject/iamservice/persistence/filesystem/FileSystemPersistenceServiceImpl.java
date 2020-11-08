package one.microproject.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemPersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemPersistenceServiceImpl.class);

    private final Path dataFile;
    private final ObjectMapper mapper;
    private final boolean flushOnChange;
    private final ModelWrapper modelWrapper;

    public FileSystemPersistenceServiceImpl(Path dataFile, boolean flushOnChange, ModelWrapper modelWrapper) {
        this.dataFile = dataFile;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.flushOnChange = flushOnChange;
        this.modelWrapper = modelWrapper;
        LOG.info("FileSystemPersistence: dataFile={}, flushOnChange={}", dataFile, flushOnChange);
    }

    @Override
    public synchronized void onModelChange(Model model) {
        flushOnChange();
    }

    @Override
    public synchronized <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        long timeStamp = System.nanoTime();
        flushOnChange();
        LOG.trace("onNodeCreated: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

    @Override
    public synchronized <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode) {
        long timeStamp = System.nanoTime();
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

}
