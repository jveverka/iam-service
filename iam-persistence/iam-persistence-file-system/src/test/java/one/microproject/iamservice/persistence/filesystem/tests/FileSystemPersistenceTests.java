package one.microproject.iamservice.persistence.filesystem.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapperImpl;
import one.microproject.iamservice.persistence.filesystem.FileSystemDataLoadServiceImpl;
import one.microproject.iamservice.persistence.filesystem.FileSystemPersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.security.Security;

import static one.microproject.iamservice.core.utils.ModelUtils.IAM_ADMINS_ORG;
import static one.microproject.iamservice.core.utils.ModelUtils.IAM_ADMINS_PROJECT;
import static one.microproject.iamservice.core.utils.ModelUtils.createDefaultModelCache;
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
    public void testPersistenceSerializationAndLoading() throws Exception {
        Path tempDirFile = Path.of(sharedTempDir.toString(), "model-data.json");
        FileSystemPersistenceServiceImpl persistenceService = new FileSystemPersistenceServiceImpl(tempDirFile);
        ModelWrapper modelWrapper = new ModelWrapperImpl(ModelUtils.DEFAULT_MODEL, persistenceService, false);
        createDefaultModelCache(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, "secret", "top-secret", "admin@email.com", modelWrapper, Boolean.FALSE);
        modelWrapper.flush();

        DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(tempDirFile);
        ModelWrapper loadedModelWrapper = dataLoadService.populateCache();
        assertNotNull(loadedModelWrapper);
        assertEquals(1, loadedModelWrapper.getOrganizations().size());
        assertEquals(1, loadedModelWrapper.getProjects().size());
        assertEquals(1, loadedModelWrapper.getClients().size());
        assertEquals(1, loadedModelWrapper.getUsers().size());
        assertEquals(3, loadedModelWrapper.getRoles().size());
    }

}
