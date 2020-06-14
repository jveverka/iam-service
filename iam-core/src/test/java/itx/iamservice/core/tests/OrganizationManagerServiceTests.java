package itx.iamservice.core.tests;

import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import itx.iamservice.core.tests.persistence.TestingPersistenceService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.Security;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrganizationManagerServiceTests {

    private static ModelCache modelCache;
    private static TestingPersistenceService testingPersistenceService;
    private static OrganizationManagerService organizationManagerService;
    private static OrganizationId oid001 = OrganizationId.from("organization-001");
    private static OrganizationId oid002 = OrganizationId.from("organization-002");

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
        testingPersistenceService = new TestingPersistenceService();
        modelCache = ModelUtils.createEmptyModelCache(testingPersistenceService, ModelId.from("model-01"), "test-model");
        organizationManagerService = new OrganizationManagerServiceImpl(modelCache);
    }

    @Test
    @Order(1)
    public void checkEmptyModelTest() {
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(0, all.size());
        assertEquals(1, testingPersistenceService.getModelsCount());
    }

    @Test
    @Order(2)
    public void createFirstOrganizationTest() throws PKIException {
        boolean result = organizationManagerService.create(oid001, CreateOrganizationRequest.from("org-001", "org-001-name"));
        assertTrue(result);
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(1, all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isPresent());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isEmpty());
        assertEquals(1, testingPersistenceService.getNodesCount());
    }

    @Test
    @Order(2)
    public void createSecondOrganizationTest() throws PKIException {
        boolean result = organizationManagerService.create(oid002, CreateOrganizationRequest.from("org-002", "org-002-name"));
        assertTrue(result);
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(2, all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isPresent());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isPresent());
        assertEquals(2, testingPersistenceService.getNodesCount());
    }

    @Test
    @Order(3)
    public void createExistingOrganizationTest() throws PKIException {
        boolean result = organizationManagerService.create(oid001, CreateOrganizationRequest.from("org-001", "org-001-name"));
        assertFalse(result);
        result = organizationManagerService.create(oid002, CreateOrganizationRequest.from("org-002", "org-002-name"));
        assertFalse(result);
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(2, all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isPresent());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isPresent());
        assertEquals(2, testingPersistenceService.getNodesCount());
    }

    @Test
    @Order(4)
    public void removeFirstOrganizationTest() {
        boolean removed = organizationManagerService.remove(oid001);
        assertTrue(removed);
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(1,all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isEmpty());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isPresent());
        assertEquals(1, testingPersistenceService.getNodesCount());
    }

    @Test
    @Order(5)
    public void removeSecondOrganizationTest() {
        boolean removed = organizationManagerService.remove(oid002);
        assertTrue(removed);
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(0,all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isEmpty());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isEmpty());
        assertEquals(0, testingPersistenceService.getNodesCount());
    }

    @Test
    @Order(6)
    public void removeNotExistingOrganizationTest() {
        boolean removed = organizationManagerService.remove(oid001);
        assertFalse(removed);
        removed = organizationManagerService.remove(oid002);
        assertFalse(removed);
    }

    @Test
    @Order(7)
    public void checkFinalEmptyModelTest() {
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(0,all.size());
        assertEquals(0, testingPersistenceService.getNodesCount());
    }

}
