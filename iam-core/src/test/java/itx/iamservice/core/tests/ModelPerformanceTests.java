package itx.iamservice.core.tests;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
import itx.iamservice.core.services.persistence.PersistenceService;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelPerformanceTests {

    private static final Logger LOG = LoggerFactory.getLogger(ModelPerformanceTests.class);

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
        PersistenceService persistenceService = new LoggingPersistenceServiceImpl();
        long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        model = ModelUtils.createModel(organizations, projects, clients, users, permissions, roles, persistenceService);
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
        ModelWrapper modelWrapper = model.export();
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
        ModelWrapper modelWrapper = model.export();
        assertEquals(organizations, modelWrapper.getOrganizations().size());
    }

}
