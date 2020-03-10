package itx.iamservice.core.tests;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.Security;
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
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
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
    public void createOrganizationsTest() throws PKIException {
        boolean result = organizationManagerService.create(oid001, "org-001");
        assertTrue(result);
        result = organizationManagerService.create(oid002, "org-002");
        assertTrue(result);
        Collection<Organization> all = organizationManagerService.getAll();
        assertTrue(all.size() == 2);
    }

    @Test
    @Order(3)
    public void createFirstProjectTest() throws PKIException {
        boolean result = projectManagerService.create(oid001, pId001, "p001");
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertTrue(all.size() == 1);
        all = projectManagerService.getAll(oid002);
        assertTrue(all.size() == 0);
    }

    @Test
    @Order(4)
    public void createSecondProjectTest() throws PKIException {
        boolean result = projectManagerService.create(oid002, pId001, "p001");
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertTrue(all.size() == 1);
        all = projectManagerService.getAll(oid002);
        assertTrue(all.size() == 1);
    }

    @Test
    @Order(5)
    public void addAndRemoveRolesTest() {
        Role role1 = new Role(RoleId.from("role-001"), "role-001");
        Role role2 = new Role(RoleId.from("role-002"), "role-002");
        Collection<Role> roles = projectManagerService.getRoles(oid001, pId001);
        assertTrue(roles.size() == 0);
        boolean result = projectManagerService.addRole(oid001, pId001, role1);
        assertTrue(result);
        result = projectManagerService.addRole(oid001, pId001, role2);
        assertTrue(result);
        roles = projectManagerService.getRoles(oid001, pId001);
        assertTrue(roles.size() == 2);
        result= projectManagerService.removeRole(oid001, pId001, role1.getId());
        assertTrue(result);
        result= projectManagerService.removeRole(oid001, pId001, role2.getId());
        assertTrue(result);
        roles = projectManagerService.getRoles(oid001, pId001);
        assertTrue(roles.size() == 0);
    }

    @Test
    @Order(6)
    public void removeFirstProject() {
        boolean result = projectManagerService.remove(oid001, pId001);
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid001);
        assertTrue(all.size() == 0);
    }

    @Test
    @Order(7)
    public void removeSecondProject() {
        boolean result = projectManagerService.remove(oid002, pId001);
        assertTrue(result);
        Collection<Project> all = projectManagerService.getAll(oid002);
        assertTrue(all.size() == 0);
    }

}
