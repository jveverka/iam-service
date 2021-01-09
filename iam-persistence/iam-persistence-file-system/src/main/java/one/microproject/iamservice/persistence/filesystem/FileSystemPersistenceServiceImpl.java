package one.microproject.iamservice.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemPersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemPersistenceServiceImpl.class);

    private final Path dataFile;
    private final ObjectMapper mapper;

    public FileSystemPersistenceServiceImpl(Path dataFile) {
        this.dataFile = dataFile;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        LOG.info("FileSystemPersistence: dataFile={}", dataFile);
    }

    @Override
    public void onModelChange(ModelWrapper modelWrapper) throws IOException {
        long timestamp = System.nanoTime();
        mapper.writeValue(dataFile.toFile(), modelWrapper);
        LOG.debug("onModelChange: {}, saved in {}ms", modelWrapper.getModel().getId(), ((System.nanoTime() - timestamp)/1_000_000F));
    }

}
