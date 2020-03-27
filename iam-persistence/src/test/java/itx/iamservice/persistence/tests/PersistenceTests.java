package itx.iamservice.persistence.tests;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.persistence.PersistenceResult;
import itx.iamservice.persistence.PersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.Security;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersistenceTests {

    private static String serialized;
    private static Model model;
    private static Model loadedModel;
    private static PersistenceServiceImpl persistenceService;

    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
        model = TestUtils.createModel();
        persistenceService = new PersistenceServiceImpl();
    }

    @Test
    @Order(1)
    public void persistenceSaveTest() throws ExecutionException, InterruptedException {
        Future<PersistenceResult> persistenceResultFuture = persistenceService.saveModel(model);
        assertNotNull(persistenceResultFuture);
        PersistenceResult persistenceResult = persistenceResultFuture.get();
        assertTrue(persistenceResult.isSuccessful());
        serialized = persistenceService.getSerialized(model.getId());
        assertNotNull(serialized);
    }

    @Test
    @Order(2)
    public void persistenceLoadTest() throws ExecutionException, InterruptedException {
        Future<Model> modelFuture = persistenceService.loadModel(model.getId());
        assertNotNull(modelFuture);
        loadedModel = modelFuture.get();
        assertNotNull(loadedModel);
    }

    @Test
    @Order(3)
    public void compareModelsTest() {
        assertEquals(model.getId(), loadedModel.getId());
        assertEquals(model.getName(), loadedModel.getName());
        assertTrue(model.getOrganizations().size() == loadedModel.getOrganizations().size());

        Optional<Organization> organizationOptional = model.getOrganizations().stream().filter(o -> TestUtils.organizationId.equals(o.getId())).findFirst();
        Optional<Organization> loadedOrganizationOptional = loadedModel.getOrganizations().stream().filter(o -> TestUtils.organizationId.equals(o.getId())).findFirst();
        assertTrue(organizationOptional.isPresent());
        assertTrue(loadedOrganizationOptional.isPresent());

        Organization organization = organizationOptional.get();
        Organization loadedOrganization = loadedOrganizationOptional.get();

        assertEquals(organization.getId(), loadedOrganization.getId());
        assertEquals(organization.getName(), loadedOrganization.getName());
        assertEquals(organization.getKeyPairSerialized(), loadedOrganization.getKeyPairSerialized());
    }

}