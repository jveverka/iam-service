package itx.iamservice.persistence.filesystem.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.persistence.DataLoadService;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
import itx.iamservice.persistence.filesystem.FileSystemDataLoadServiceImpl;
import itx.iamservice.persistence.filesystem.FileSystemPersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        FileSystemPersistenceServiceImpl persistenceService = new FileSystemPersistenceServiceImpl(sharedTempDir, false);
        ModelUtils.createDefaultModelCache("secret", "top-secret", persistenceService);
        String serializedModel = persistenceService.flushToString();
        assertNotNull(serializedModel);

        ModelWrapper modelWrapper = mapper.readValue(serializedModel, ModelWrapper.class);
        assertNotNull(modelWrapper);
        assertEquals(1, modelWrapper.getOrganizations().size());
        assertEquals(1, modelWrapper.getProjects().size());
        assertEquals(1, modelWrapper.getClients().size());
        assertEquals(1, modelWrapper.getUsers().size());
        assertEquals(7, modelWrapper.getRoles().size());

        DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(serializedModel, persistenceService);
        ModelCache modelCache = dataLoadService.populateCache();
        assertNotNull(modelCache);
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Path dataFilePath = Paths.get("/tmp/iam-data.json");
        FileSystemPersistenceServiceImpl persistenceService = new FileSystemPersistenceServiceImpl(dataFilePath, true);
        long timeStamp = System.nanoTime();
        ModelCache modelCache = ModelUtils.createModel(3, 3, 4, 100, 5, 3, persistenceService);
        LOG.info("model create time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        timeStamp = System.nanoTime();
        persistenceService.flush();
        LOG.info("model flush time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        assertNotNull(modelCache);
        DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(dataFilePath, persistenceService);
        timeStamp = System.nanoTime();
        modelCache = dataLoadService.populateCache();
        LOG.info("model load time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        assertNotNull(modelCache);
    }

}
