package itx.iamservice.persistence.inmemory.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.persistence.DataLoadService;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
import itx.iamservice.persistence.inmemory.InMemoryDataLoadServiceImpl;
import itx.iamservice.persistence.inmemory.InMemoryPersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryPersistenceTests {

    static ObjectMapper mapper;

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
        mapper = new ObjectMapper();
    }

    @Test
    public void testPersistenceSerializationAndLoading() throws Exception {
        InMemoryPersistenceServiceImpl persistenceService = new InMemoryPersistenceServiceImpl();
        ModelUtils.createDefaultModelCache("secret", "top-secret", persistenceService);
        String serializedModel = persistenceService.flushToString();
        assertNotNull(serializedModel);

        ModelWrapper modelWrapper = mapper.readValue(serializedModel, ModelWrapper.class);
        assertNotNull(modelWrapper);
        assertEquals(1, modelWrapper.getOrganizations().size());
        assertEquals(1, modelWrapper.getProjects().size());
        assertEquals(1, modelWrapper.getClients().size());
        assertEquals(1, modelWrapper.getUsers().size());
        assertEquals(2, modelWrapper.getRoles().size());

        DataLoadService dataLoadService = new InMemoryDataLoadServiceImpl(modelWrapper, persistenceService);
        ModelCache modelCache = dataLoadService.populateCache();
        assertNotNull(modelCache);
    }

}
