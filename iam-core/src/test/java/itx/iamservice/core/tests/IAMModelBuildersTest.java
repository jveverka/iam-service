package itx.iamservice.core.tests;

import itx.iamservice.core.IAMModelBuilders;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Role;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IAMModelBuildersTest {

    private static Role adminFullAccess;
    private static Role adminReadAccess;

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
        Model model = IAMModelBuilders.modelBuilder("my model")
                .addOrganization("first organization")
                    .addProject("first project")
                        .addRole(adminFullAccess)
                        .addRole(adminReadAccess)
                        .addClient("test client")
                            .addRole(adminReadAccess.getId())
                        .and()
                        .addUser("admin user")
                            .addRole(adminFullAccess.getId())
                            .addUserNamePasswordCredentials("admin", "secret")
                        .and()
                    .and()
                    .addProject("second project")
                .and().and()
                .addOrganization("second organization")
                    .addProject("first project")
                    .and()
                    .addProject("second project")
                .build();
        assertNotNull(model);

    }

}
