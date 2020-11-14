package one.microproject.iamservice.core.tests;

import one.microproject.iamservice.core.IAMCoreBuilder;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IAMCoreBuilderTests {

    @Test
    public void testMinimalBuilder() throws Exception {
        IAMCoreBuilder.IAMCore iamCore = IAMCoreBuilder.builder()
                .withBCProvider()
                .withDefaultModel("secret", "top-secret", "admin@email.com")
                .build();
        assertNotNull(iamCore);
        assertNotNull(iamCore.getClientManagementService());
        assertNotNull(iamCore.getOrganizationManagerService());
        assertNotNull(iamCore.getModelCache());
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
                .withDefaultModel("secret", "top-secret", "admin@email.com")
                .withDefaultAuthorizationCodeCache(20L, TimeUnit.MINUTES)
                .build();
        assertNotNull(iamCore);
        assertNotNull(iamCore.getClientManagementService());
        assertNotNull(iamCore.getOrganizationManagerService());
        assertNotNull(iamCore.getModelCache());
        assertNotNull(iamCore.getProjectManagerService());
        assertNotNull(iamCore.getResourceServerService());
        assertNotNull(iamCore.getUserManagerService());
        assertNotNull(iamCore.getAuthenticationService());
        iamCore.close();
    }

}
