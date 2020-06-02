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

import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileSystemPersistenceTests {

    @TempDir
    static Path sharedTempDir;
    static ObjectMapper mapper;

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
        mapper = new ObjectMapper();
    }

    @Test
    public void testPersistenceSerializationAndLoading() throws PKIException, IOException {
        FileSystemPersistenceServiceImpl persistenceService = new FileSystemPersistenceServiceImpl(sharedTempDir, false);
        ModelUtils.createDefaultModelCache("secret", persistenceService);
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

}
