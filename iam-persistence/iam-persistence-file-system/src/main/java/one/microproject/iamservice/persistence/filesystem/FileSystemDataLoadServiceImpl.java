package one.microproject.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemDataLoadServiceImpl implements DataLoadService {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemDataLoadServiceImpl.class);

    private Path dataFile;
    private String serializedModel;

    public FileSystemDataLoadServiceImpl(Path dataFile) {
        LOG.info("FileSystemPersistence: loading from dataFile={}", dataFile);
        this.dataFile = dataFile;
        this.serializedModel = null;
    }

    public FileSystemDataLoadServiceImpl(String serializedModel) {
        this.dataFile = null;
        this.serializedModel = serializedModel;
    }

    @Override
    public ModelWrapper populateCache() throws IOException {
        long timeStamp = System.nanoTime();
        ObjectMapper mapper = new ObjectMapper();
        if (dataFile != null) {
            ModelWrapper modelWrapper = mapper.readValue(dataFile.toFile(), ModelWrapperImpl.class);
            LOG.trace("populateCache: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
            return modelWrapper;
        } else if (serializedModel != null) {
            ModelWrapper modelWrapper = mapper.readValue(serializedModel, ModelWrapperImpl.class);
            LOG.trace("populateCache: {}ms", ((System.nanoTime() - timeStamp)/1_000_000F));
            return modelWrapper;
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
