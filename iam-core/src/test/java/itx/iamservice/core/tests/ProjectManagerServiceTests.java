package itx.iamservice.core.tests;

import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.RoleImpl;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
import itx.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectManagerServiceTests {

    private static ModelCache modelCache;
    private static TestingPersistenceService testingPersistenceService;
    private static OrganizationManagerService organizationManagerService;
    private static ProjectManagerService projectManagerService;
    private static OrganizationId oid001;
    private static OrganizationId oid002;
    private static ProjectId pId001 = ProjectId.from("projectid-001");

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
        testingPersistenceService = new TestingPersistenceService();
        modelCache = ModelUtils.createEmptyModelCache(testingPersistenceService, ModelId.from("model-01"), "test-model");
        organizationManagerService = new OrganizationManagerServiceImpl(modelCache);
        projectManagerService = new ProjectManagerServiceImpl(modelCache);
    }

    @Test
    @Order(1)
    public void checkEmptyModelTest() {
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(0, all.size());
        Optional<Project> projectOptional = projectManagerService.get(oid001, pId001);
        assertTrue(projectOptional.isEmpty());
        projectOptional = projectManagerService.get(oid002, pId001);
        assertTrue(projectOptional.isEmpty());
    }

    @Test
    @Order(2)
    public void createOrganizationsTest() throws PKIException {
        Optional<OrganizationId> result = organizationManagerService.create(CreateOrganizationRequest.from("org-001", "org-001-name"));
        assertTrue(result.isPresent());
        oid001 = result.get();
        result = organizationManagerService.create(CreateOrganizationRequest.from("org-002", "org-002-name"));
        assertTrue(result.isPresent());
        oid002 = result.get();
        Collection<Organization> all = organizationManagerService.getAll();
        assertEquals(2, all.size());
    }

    @Test
    @Order(3)
    public void createFirstProjectTest() throws PKIException {
        Optional<Project> result = projectManagerService.create(oid001, CreateProjectRequest.from(pId001, "p001-name"));
        assertTrue(result.isPresent());
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertEquals(1, all.size());
        all = projectManagerService.getAll(oid002);
        assertEquals(0, all.size());
    }

    @Test
    @Order(4)
    public void createSecondProjectTest() throws PKIException {
        Optional<Project> result = projectManagerService.create(oid002, CreateProjectRequest.from(pId001, "p001-name"));
        assertTrue(result.isPresent());
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertEquals(1, all.size());
        all = projectManagerService.getAll(oid002);
        assertEquals(1, all.size());
    }

    @Test
    @Order(5)
    public void addAndRemoveRolesTest() {
        CreateRoleRequest request1 = new CreateRoleRequest(RoleId.from("role-001"), "role-001");
        CreateRoleRequest request2 = new CreateRoleRequest(RoleId.from("role-002"), "role-002");
        Collection<Role> roles = projectManagerService.getRoles(oid001, pId001);
        assertEquals(0, roles.size());
        Optional<RoleId> result = projectManagerService.addRole(oid001, pId001, request1);
        assertTrue(result.isPresent());
        result = projectManagerService.addRole(oid001, pId001, request2);
        assertTrue(result.isPresent());
        roles = projectManagerService.getRoles(oid001, pId001);
        assertEquals(2, roles.size());
        boolean removeResult = projectManagerService.removeRole(oid001, pId001, request1.getId());
        assertTrue(removeResult);
        removeResult = projectManagerService.removeRole(oid001, pId001, request2.getId());
        assertTrue(removeResult);
        roles = projectManagerService.getRoles(oid001, pId001);
        assertEquals(0, roles.size());
    }

    @Test
    @Order(6)
    public void removeFirstProject() {
        boolean result = projectManagerService.remove(oid001, pId001);
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertEquals(0, all.size());
    }

    @Test
    @Order(7)
    public void removeSecondProject() {
        boolean result = projectManagerService.remove(oid002, pId001);
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid002);
        assertEquals(0, all.size());
    }

}
