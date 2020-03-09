package itx.iamservice.core.tests;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectManagerServiceTests {

    private static Model model;
    private static OrganizationManagerService organizationManagerService;
    private static ProjectManagerService projectManagerService;
    private static OrganizationId oid001 = OrganizationId.from("organization-001");
    private static OrganizationId oid002 = OrganizationId.from("organization-002");
    private static ProjectId pId001 = ProjectId.from("projectid-001");

    @BeforeAll
    private static void init() throws NoSuchAlgorithmException {
        model = new ModelImpl();
        organizationManagerService = new OrganizationManagerServiceImpl(model);
        projectManagerService = new ProjectManagerServiceImpl(model);
    }

    @Test
    @Order(1)
    public void checkEmptyModelTest() {
        Collection<Organization> all = organizationManagerService.getAll();
        assertTrue(all.size() == 0);
        Optional<Project> projectOptional = projectManagerService.get(oid001, pId001);
        assertTrue(projectOptional.isEmpty());
        projectOptional = projectManagerService.get(oid002, pId001);
        assertTrue(projectOptional.isEmpty());
    }

    @Test
    @Order(2)
    public void createOrganizationsTest() {
        boolean result = organizationManagerService.create(oid001, "org-001");
        assertTrue(result);
        result = organizationManagerService.create(oid002, "org-002");
        assertTrue(result);
        Collection<Organization> all = organizationManagerService.getAll();
        assertTrue(all.size() == 2);
    }

    @Test
    @Order(3)
    public void createFirstProjectTest() {
        boolean result = projectManagerService.create(oid001, pId001, "p001");
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertTrue(all.size() == 1);
        all = projectManagerService.getAll(oid002);
        assertTrue(all.size() == 0);
    }

    @Test
    @Order(4)
    public void createSecondProjectTest() {
        boolean result = projectManagerService.create(oid002, pId001, "p001");
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertTrue(all.size() == 1);
        all = projectManagerService.getAll(oid002);
        assertTrue(all.size() == 1);
    }

}
