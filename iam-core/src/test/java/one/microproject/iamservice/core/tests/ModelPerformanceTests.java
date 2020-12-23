package one.microproject.iamservice.core.tests;

import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Security;
import java.util.Collection;
import java.util.Optional;

import static one.microproject.iamservice.core.utils.ModelUtils.createInMemoryModelWrapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelPerformanceTests {

    private static final Logger LOG = LoggerFactory.getLogger(ModelPerformanceTests.class);

    private static ModelWrapper modelWrapper;
    private static int organizations = 3;
    private static int projects = 3;
    private static int clients = 4;
    private static int users = 3;
    private static int permissions = 5;
    private static int roles = 2;

    private static ModelCache model;

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    @Order(1)
    public void generateBigModel() throws PKIException {
        modelWrapper = createInMemoryModelWrapper("default");
        long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        model = ModelUtils.createModel(organizations, projects, clients, users, permissions, roles, modelWrapper);
        long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOG.info("memory allocated for model = {}Mb", (((memAfter - memBefore)/1024F)/1024F));
        assertNotNull(model);
        assertNotNull(model.getModel());
        assertNotNull(model.getModel().getId());
        assertNotNull(model.getModel().getName());
        assertEquals(organizations,  model.getOrganizations().size());
    }

    @Test
    @Order(2)
    public void checkModelCache() {
        assertNotNull(modelWrapper);
        assertNotNull(modelWrapper.getModel());
        assertEquals(organizations, modelWrapper.getOrganizations().size());
        assertEquals(organizations*projects, modelWrapper.getProjects().size());
        assertEquals(organizations*projects*clients, modelWrapper.getClients().size());
        assertEquals(organizations*projects*users, modelWrapper.getUsers().size());
        assertEquals(organizations*projects*roles, modelWrapper.getRoles().size());
    }
    
    @Test
    @Order(3)
    public void checkProjects() {
        Optional<Organization> organization = model.getOrganization(OrganizationId.from("organization-0"));
        assertTrue(organization.isPresent());
        Collection<ProjectId> projectsCollection = organization.get().getProjects();
        assertNotNull(projectsCollection);
        assertEquals(projects, projectsCollection.size());
    }

    @Test
    @Order(4)
    public void removeOrganizationWithExistingProjects() {
        boolean result = model.remove(OrganizationId.from("organization-0"));
        assertFalse(result);
        assertEquals(organizations, modelWrapper.getOrganizations().size());
    }

    @Test
    @Order(5)
    public void removeProjectWithExistingUsersAndClientsAndRoles() {
        boolean result = model.remove(OrganizationId.from("organization-0"), ProjectId.from("project-0"));
        assertFalse(result);
        assertEquals(organizations*projects, modelWrapper.getProjects().size());
    }

    @Test
    @Order(6)
    public void removeRoleInUse() {
        boolean result = model.remove(OrganizationId.from("organization-0"), ProjectId.from("project-0"), RoleId.from("role-0"));
        assertFalse(result);
        assertEquals(organizations*projects*roles, modelWrapper.getRoles().size());
    }

    @Test
    @Order(7)
    public void removePermissionInUse() {
        boolean result = model.removePermission(OrganizationId.from("organization-0"), ProjectId.from("project-0"), PermissionId.from("service1.resource-0.action"));
        assertFalse(result);
    }

    @Test
    @Order(8)
    public void removeAllUsers() {
        for (int organizationsIndex=0; organizationsIndex<organizations; organizationsIndex++) {
            for (int projectIndex=0; projectIndex<projects; projectIndex++) {
                for (int userIndex=0; userIndex<users; userIndex++) {
                    boolean result = model.remove(OrganizationId.from("organization-" + organizationsIndex),
                            ProjectId.from("project-" + projectIndex), UserId.from("user-" + userIndex));
                    assertTrue(result);
                }
            }
        }
        assertEquals(organizations, modelWrapper.getOrganizations().size());
        assertEquals(organizations*projects, modelWrapper.getProjects().size());
        assertEquals(organizations*projects*clients, modelWrapper.getClients().size());
        assertEquals(0, modelWrapper.getUsers().size());
        assertEquals(organizations*projects*roles, modelWrapper.getRoles().size());
    }

    @Test
    @Order(9)
    public void removeAllClients() {
        for (int organizationsIndex=0; organizationsIndex<organizations; organizationsIndex++) {
            for (int projectIndex=0; projectIndex<projects; projectIndex++) {
                for (int clientIndex=0; clientIndex<clients; clientIndex++) {
                    boolean result = model.remove(OrganizationId.from("organization-" + organizationsIndex),
                            ProjectId.from("project-" + projectIndex), ClientId.from("client-" + clientIndex));
                    assertTrue(result);
                }
            }
        }
        assertEquals(organizations, modelWrapper.getOrganizations().size());
        assertEquals(organizations*projects, modelWrapper.getProjects().size());
        assertEquals(0, modelWrapper.getClients().size());
        assertEquals(0, modelWrapper.getUsers().size());
        assertEquals(organizations*projects*roles, modelWrapper.getRoles().size());
    }

    @Test
    @Order(10)
    public void removeAllRoles() {
        for (int organizationsIndex=0; organizationsIndex<organizations; organizationsIndex++) {
            for (int projectIndex=0; projectIndex<projects; projectIndex++) {
                for (int roleIndex=0; roleIndex<roles; roleIndex++) {
                    boolean result = model.remove(OrganizationId.from("organization-" + organizationsIndex),
                            ProjectId.from("project-" + projectIndex), RoleId.from("role-" + roleIndex));
                    assertTrue(result);
                }
            }
        }
        assertEquals(organizations, modelWrapper.getOrganizations().size());
        assertEquals(organizations*projects, modelWrapper.getProjects().size());
        assertEquals(0, modelWrapper.getClients().size());
        assertEquals(0, modelWrapper.getUsers().size());
        assertEquals(0, modelWrapper.getRoles().size());
    }

    @Test
    @Order(11)
    public void removeAllPermissions() {
        for (int organizationsIndex=0; organizationsIndex<organizations; organizationsIndex++) {
            for (int projectIndex=0; projectIndex<projects; projectIndex++) {
                for (int permissionIndex=0; permissionIndex<permissions; permissionIndex++) {
                    boolean result = model.removePermission(OrganizationId.from("organization-" + organizationsIndex),
                            ProjectId.from("project-" + projectIndex), PermissionId.from("permission-" + permissionIndex));
                    assertTrue(result);
                }
            }
        }
        assertEquals(organizations, modelWrapper.getOrganizations().size());
        assertEquals(organizations*projects, modelWrapper.getProjects().size());
        assertEquals(0, modelWrapper.getClients().size());
        assertEquals(0, modelWrapper.getUsers().size());
        assertEquals(0, modelWrapper.getRoles().size());
    }

    @Test
    @Order(12)
    public void removeAllProjects() {
        for (int organizationsIndex=0; organizationsIndex<organizations; organizationsIndex++) {
            for (int projectIndex=0; projectIndex<projects; projectIndex++) {
                 boolean result = model.remove(OrganizationId.from("organization-" + organizationsIndex),
                         ProjectId.from("project-" + projectIndex));
                 assertTrue(result);
            }
        }
        assertEquals(organizations, modelWrapper.getOrganizations().size());
        assertEquals(0, modelWrapper.getProjects().size());
        assertEquals(0, modelWrapper.getClients().size());
        assertEquals(0, modelWrapper.getUsers().size());
        assertEquals(0, modelWrapper.getRoles().size());
    }

    @Test
    @Order(13)
    public void removeAllOrganizations() {
        for (int organizationsIndex=0; organizationsIndex<organizations; organizationsIndex++) {
             boolean result = model.remove(OrganizationId.from("organization-" + organizationsIndex));
             assertTrue(result);
        }
        assertEquals(0, modelWrapper.getProjects().size());
        assertEquals(0, modelWrapper.getOrganizations().size());
        assertEquals(0, modelWrapper.getUsers().size());
        assertEquals(0, modelWrapper.getClients().size());
        assertEquals(0, modelWrapper.getRoles().size());
    }

}
