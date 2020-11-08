package one.microproject.iamservice.persistence.filesystem.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapperImpl;
import one.microproject.iamservice.persistence.filesystem.FileSystemDataLoadServiceImpl;
import one.microproject.iamservice.persistence.filesystem.FileSystemPersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileSystemPersistenceTests {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemPersistenceTests.class);

    @TempDir
    static Path sharedTempDir;
    static ObjectMapper mapper;

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
        mapper = new ObjectMapper();
    }

    @Test
    public void testPersistenceSerializationAndLoading() throws Exception {
        ModelCache cache = ModelUtils.createDefaultModelCache("secret", "top-secret", "admin@email.com");
        FileSystemPersistenceServiceImpl persistenceService = new FileSystemPersistenceServiceImpl(sharedTempDir, false, cache.export());
        String serializedModel = persistenceService.flushToString();
        assertNotNull(serializedModel);

        ModelWrapper modelWrapper = mapper.readValue(serializedModel, ModelWrapperImpl.class);
        assertNotNull(modelWrapper);
        assertEquals(1, modelWrapper.getOrganizations().size());
        assertEquals(1, modelWrapper.getProjects().size());
        assertEquals(1, modelWrapper.getClients().size());
        assertEquals(1, modelWrapper.getUsers().size());
        assertEquals(3, modelWrapper.getRoles().size());

        DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(serializedModel);
        ModelWrapper modelCache = dataLoadService.populateCache();
        assertNotNull(modelCache);
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Path dataFilePath = Paths.get("/tmp/iam-data.json");
        long timeStamp = System.nanoTime();
        ModelCache modelCache = ModelUtils.createModel(3, 3, 4, 100, 5, 3);
        FileSystemPersistenceServiceImpl persistenceService = new FileSystemPersistenceServiceImpl(dataFilePath, true, modelCache.export());
        LOG.info("model create time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        timeStamp = System.nanoTime();
        persistenceService.flush();
        LOG.info("model flush time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        assertNotNull(modelCache);
        DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(dataFilePath);
        timeStamp = System.nanoTime();
        ModelWrapper cache = dataLoadService.populateCache();
        LOG.info("model load time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        assertNotNull(cache);
    }

}
