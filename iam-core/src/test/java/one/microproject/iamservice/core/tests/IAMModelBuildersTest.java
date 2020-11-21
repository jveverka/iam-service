package one.microproject.iamservice.core.tests;

import one.microproject.iamservice.core.IAMModelBuilders;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.Security;
import java.util.Set;

import static one.microproject.iamservice.core.model.utils.ModelUtils.createInMemoryModelWrapper;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IAMModelBuildersTest {

    private static Role adminFullAccess;
    private static Role adminReadAccess;
    private static Set<String> audience = Set.of("audience");

    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    @Order(1)
    public void testRoleBuilders() {
        adminFullAccess = IAMModelBuilders.roleBuilder("Admin Full Access Role")
                .addPermission("administration", "user-management", "create")
                .addPermission("administration", "user-management", "delete")
                .addPermission("administration", "user-management", "update")
                .addPermission("administration", "user-management", "read")
                .build();
        adminReadAccess = IAMModelBuilders.roleBuilder("Admin Read Access Role")
                .addPermission("administration", "user-management", "read")
                .build();
    }

    @Test
    @Order(2)
    public void testModelBuilder() throws PKIException {
        ModelCache modelCache = IAMModelBuilders.modelBuilder(createInMemoryModelWrapper("my model"))
                .addOrganization("first organization")
                    .addProject("first project", audience)
                        .addRole(adminFullAccess)
                        .addRole(adminReadAccess)
                        .addClient("test client", "http://localhost:8080")
                            .addRole(adminReadAccess.getId())
                        .and()
                        .addUser("admin user", "admin@email.com")
                            .addRole(adminFullAccess.getId())
                            .addUserNamePasswordCredentials("admin", "secret")
                        .and()
                    .and()
                    .addProject("second project", audience)
                .and().and()
                .addOrganization("second organization")
                    .addProject("first project", audience)
                    .and()
                    .addProject("second project", audience)
                .build();
        assertNotNull(modelCache);
    }

    @Test
    @Order(3)
    public void testRoleBuildersWithIds() {
        adminFullAccess = IAMModelBuilders.roleBuilder(RoleId.from("role-001"),"Admin Full Access Role")
                .addPermission("administration", "user-management", "create")
                .addPermission("administration", "user-management", "delete")
                .addPermission("administration", "user-management", "update")
                .addPermission("administration", "user-management", "read")
                .build();
        adminReadAccess = IAMModelBuilders.roleBuilder(RoleId.from("role-002"),"Admin Read Access Role")
                .addPermission("administration", "user-management", "read")
                .build();
    }

    @Test
    @Order(4)
    public void testModelBuilderWithIds() throws PKIException {
        ModelCache modelCache = IAMModelBuilders.modelBuilder(createInMemoryModelWrapper("model-001", "my model"))
                .addOrganization(OrganizationId.from("org-001"), "first organization")
                    .addProject(ProjectId.from("project-001"), "first project", audience)
                    .addRole(adminFullAccess)
                    .addRole(adminReadAccess)
                    .addClient(ClientId.from("client-001"), "test client", "http://localhost:8080")
                        .addRole(adminReadAccess.getId())
                    .and()
                    .addUser(UserId.from("user-001"), "admin user", "admin@email.com", UserProperties.getDefault())
                        .addRole(adminFullAccess.getId())
                        .addUserNamePasswordCredentials(UserId.from("user-001"), "secret")
                    .and()
                .and()
                    .addProject(ProjectId.from("project-002"), "second project", audience)
                .and().and()
                .addOrganization(OrganizationId.from("org-002"),"second organization")
                    .addProject(ProjectId.from("project-001"),"first project", audience)
                    .and()
                    .addProject(ProjectId.from("project-002"),"second project", audience)
                .build();
        assertNotNull(modelCache);
    }
}
