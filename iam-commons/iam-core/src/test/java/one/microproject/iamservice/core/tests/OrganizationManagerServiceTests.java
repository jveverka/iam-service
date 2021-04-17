package one.microproject.iamservice.core.tests;

import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.admin.OrganizationManagerService;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateOrganizationRequest;
import one.microproject.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import one.microproject.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
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
class OrganizationManagerServiceTests {

    private static ModelCache modelCache;
    private static OrganizationManagerService organizationManagerService;
    private static OrganizationId oid001;
    private static OrganizationId oid002;

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
        modelCache = ModelUtils.createEmptyModelCache(new LoggingPersistenceServiceImpl(), ModelId.from("model-01"), "test-model");
        organizationManagerService = new OrganizationManagerServiceImpl(modelCache);
    }

    @Test
    @Order(1)
    void checkEmptyModelTest() {
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(0, all.size());
    }

    @Test
    @Order(2)
    void createFirstOrganizationTest() throws PKIException {
        Optional<OrganizationId> result = organizationManagerService.create(CreateOrganizationRequest.from("org-001", "org-001-name"));
        assertTrue(result.isPresent());
        oid001 = result.get();
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(1, all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isPresent());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isEmpty());
    }

    @Test
    @Order(2)
    void createSecondOrganizationTest() throws PKIException {
        Optional<OrganizationId> result = organizationManagerService.create(CreateOrganizationRequest.from("org-002", "org-002-name"));
        oid002 = result.get();
        assertTrue(result.isPresent());
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(2, all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isPresent());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isPresent());
    }

    @Test
    @Order(3)
    void createExistingOrganizationTest() throws PKIException {
        Optional<OrganizationId> result = organizationManagerService.create(CreateOrganizationRequest.from("org-001", "org-001-name"));
        assertFalse(result.isPresent());
        result = organizationManagerService.create(CreateOrganizationRequest.from("org-002", "org-002-name"));
        assertFalse(result.isPresent());
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(2, all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isPresent());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isPresent());
    }

    @Test
    @Order(4)
    void removeFirstOrganizationTest() {
        boolean removed = organizationManagerService.remove(oid001);
        assertTrue(removed);
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(1,all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isEmpty());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isPresent());
    }

    @Test
    @Order(5)
    void removeSecondOrganizationTest() {
        boolean removed = organizationManagerService.remove(oid002);
        assertTrue(removed);
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(0,all.size());
        Optional<Organization> organization = organizationManagerService.get(oid001);
        assertTrue(organization.isEmpty());
        organization = organizationManagerService.get(oid002);
        assertTrue(organization.isEmpty());
    }

    @Test
    @Order(6)
    void removeNotExistingOrganizationTest() {
        boolean removed = organizationManagerService.remove(oid001);
        assertFalse(removed);
        removed = organizationManagerService.remove(oid002);
        assertFalse(removed);
    }

    @Test
    @Order(7)
    void checkFinalEmptyModelTest() {
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(0,all.size());
    }

}
