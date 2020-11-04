package one.microproject.iamservice.persistence.inmemory.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.persistence.inmemory.InMemoryDataLoadServiceImpl;
import one.microproject.iamservice.persistence.inmemory.InMemoryPersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        ModelUtils.createDefaultModelCache("secret", "top-secret", "admin@email.com", persistenceService);
        String serializedModel = persistenceService.flushToString();
        assertNotNull(serializedModel);

        ModelWrapper modelWrapper = mapper.readValue(serializedModel, ModelWrapper.class);
        assertNotNull(modelWrapper);
        assertEquals(1, modelWrapper.getOrganizations().size());
        assertEquals(1, modelWrapper.getProjects().size());
        assertEquals(1, modelWrapper.getClients().size());
        assertEquals(1, modelWrapper.getUsers().size());
        assertEquals(3, modelWrapper.getRoles().size());

        DataLoadService dataLoadService = new InMemoryDataLoadServiceImpl(modelWrapper, persistenceService);
        ModelCache modelCache = dataLoadService.populateCache();
        assertNotNull(modelCache);
    }

}
