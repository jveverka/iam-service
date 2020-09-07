package itx.iamservice.core.tests;

import itx.iamservice.core.IAMCoreBuilder;
import itx.iamservice.core.model.PKIException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IAMCoreBuilderTests {

    @Test
    public void testMinimalBuilder() throws Exception {
        IAMCoreBuilder.IAMCore iamCore = IAMCoreBuilder.builder()
                .withBCProvider()
                .withDefaultModel("secret", "top-secret")
                .build();
        assertNotNull(iamCore);
        assertNotNull(iamCore.getModel());
        assertNotNull(iamCore.getClientManagementService());
        assertNotNull(iamCore.getOrganizationManagerService());
        assertNotNull(iamCore.getPersistenceService());
        assertNotNull(iamCore.getProjectManagerService());
        assertNotNull(iamCore.getResourceServerService());
        assertNotNull(iamCore.getUserManagerService());
        assertNotNull(iamCore.getAuthenticationService());
        iamCore.close();
    }

    @Test
    public void testBuilder() throws Exception {
        IAMCoreBuilder.IAMCore iamCore = IAMCoreBuilder.builder()
                .withBCProvider()
                .withDefaultModel("secret", "top-secret")
                .withDefaultAuthorizationCodeCache(20L, TimeUnit.MINUTES)
                .build();
        assertNotNull(iamCore);
        assertNotNull(iamCore.getModel());
        assertNotNull(iamCore.getClientManagementService());
        assertNotNull(iamCore.getOrganizationManagerService());
        assertNotNull(iamCore.getPersistenceService());
        assertNotNull(iamCore.getProjectManagerService());
        assertNotNull(iamCore.getResourceServerService());
        assertNotNull(iamCore.getUserManagerService());
        assertNotNull(iamCore.getAuthenticationService());
        iamCore.close();
    }

}
