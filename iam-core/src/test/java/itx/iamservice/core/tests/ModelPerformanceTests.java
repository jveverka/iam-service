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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelPerformanceTests {

    private static final Logger LOG = LoggerFactory.getLogger(ModelPerformanceTests.class);

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
        model = ModelUtils.createModel(3, 3, 4, 3, 5, 3, persistenceService);
        long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOG.info("memory allocated for model = {}Mb", (((memAfter - memBefore)/1024F)/1024F));
        assertNotNull(model);
        assertNotNull(model.getModel());
        assertNotNull(model.getModel().getId());
        assertNotNull(model.getModel().getName());
        assertEquals(3,  model.getOrganizations().size());
    }

    @Test
    @Order(2)
    public void checkModelCache() {
        ModelWrapper modelWrapper = model.export();
        assertNotNull(modelWrapper);
        assertNotNull(modelWrapper.getModel());
        assertEquals(3, modelWrapper.getOrganizations().size());
        assertEquals(3*3, modelWrapper.getProjects().size());
        assertEquals(3*3*4, modelWrapper.getClients().size());
        assertEquals(3*3*3, modelWrapper.getUsers().size());
        assertEquals(3*3*3, modelWrapper.getRoles().size());
    }
    
    @Test
    @Order(3)
    public void checkProjects() {
        Optional<Organization> organization = model.getOrganization(OrganizationId.from("organization-0"));
        assertTrue(organization.isPresent());
        Collection<ProjectId> projects = organization.get().getProjects();
        assertNotNull(projects);
        assertEquals(3, projects.size());
    }

}
