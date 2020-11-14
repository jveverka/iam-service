package one.microproject.iamservice.persistence.mongo.tests;

import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.OrganizationImpl;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.RoleImpl;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateProjectRequest;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.persistence.mongo.MongoConfiguration;
import one.microproject.iamservice.persistence.mongo.MongoModelWrapperImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Security;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MongoPersistenceTests {

    private static final Logger LOG = LoggerFactory.getLogger(MongoPersistenceTests.class);

    private static OrganizationId organizationId01 = OrganizationId.from("org-001");
    private static OrganizationId organizationId02 = OrganizationId.from("org-002");
    private static ProjectId projectId01 = ProjectId.from("proj-001");
    private static ProjectId projectId02 = ProjectId.from("proj-002");

    private static ModelCache modelCache;
    private static ModelWrapper modelWrapper;

    @Container
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.2.9");

    @BeforeAll
    public static void init() {
        Security.addProvider(new BouncyCastleProvider());
        List<Integer> exposedPorts = mongoDBContainer.getExposedPorts();
        Integer port = exposedPorts.get(0);
        String replicaSetUrl = mongoDBContainer.getReplicaSetUrl();
        Integer boundPort = mongoDBContainer.getMappedPort(port);
        LOG.info("mongodb port {}", port);
        LOG.info("mongodb replicaSetUrl {}", replicaSetUrl);
        LOG.info("mongodb boundPort {}", boundPort);
        MongoConfiguration mongoConfiguration = new MongoConfiguration("localhost", boundPort,  "iam-service");
        modelWrapper = new MongoModelWrapperImpl(mongoConfiguration);
        modelCache = new ModelCacheImpl(modelWrapper);
    }

    @Test
    @Order(1)
    public void testInitialCache() throws Exception {
        modelCache.flush();
    }

    @Test
    @Order(10)
    public void testModelInfoReadWrite() {
        Model model = ModelUtils.DEFAULT_MODEL;
        modelCache.setModel(model);
        Model modelFromDb = modelCache.getModel();
        assertNotNull(modelFromDb);
        assertEquals(model.getId(), modelFromDb.getId());
        assertEquals(model.getName(), modelFromDb.getName());
    }

    @Test
    @Order(11)
    public void testCreateOrganizations() throws PKIException {
        Organization organization = new OrganizationImpl(organizationId01, "");
        Optional<OrganizationId> idOptional = modelCache.add(organization);
        assertTrue(idOptional.isPresent());

        Optional<Organization> organizationOptional = modelCache.getOrganization(organizationId01);
        assertTrue(organizationOptional.isPresent());
        assertEquals(organizationId01, organizationOptional.get().getId());

        Collection<Organization> organizations = modelCache.getOrganizations();
        assertEquals(1, organizations.size());

        Set<ModelKey<Organization>> organizationsKeys = modelWrapper.getOrganizationsKeys();
        assertEquals(1, organizationsKeys.size());

        organization = new OrganizationImpl(organizationId02, "");
        idOptional = modelCache.add(organization);
        assertTrue(idOptional.isPresent());

        organizations = modelCache.getOrganizations();
        assertEquals(2, organizations.size());

        organizationsKeys = modelWrapper.getOrganizationsKeys();
        assertEquals(2, organizationsKeys.size());
    }

    @Test
    @Order(12)
    public void testCreateProjects() throws PKIException {
        Optional<Project> projectOptional = modelCache.add(organizationId01, new CreateProjectRequest(projectId01, "", Set.of()));
        assertTrue(projectOptional.isPresent());

        projectOptional = modelCache.getProject(organizationId01, projectId01);
        assertTrue(projectOptional.isPresent());
        assertEquals(projectId01, projectOptional.get().getId());

        Collection<Project> projects = modelCache.getProjects(organizationId01);
        assertEquals(1, projects.size());
    }

    @Test
    @Order(13)
    public void testCreateRolesOnProject() throws PKIException {
        Optional<RoleId> roleIdOptional = modelCache.add(organizationId01, projectId01, new RoleImpl(RoleId.from("r-01"), "", Collections.EMPTY_LIST));
        assertTrue(roleIdOptional.isPresent());
    }

    @Test
    @Order(37)
    public void testRemoveRolesFromProject() throws PKIException {
        boolean removed = modelCache.remove(organizationId01, projectId01, RoleId.from("r-01"));
        assertTrue(removed);

        removed = modelCache.remove(organizationId01, projectId01, RoleId.from("r-01"));
        assertFalse(removed);
    }

    @Test
    @Order(38)
    public void testRemoveProjects() throws PKIException {
        boolean removed = modelCache.remove(organizationId01, projectId01);
        assertTrue(removed);

        removed = modelCache.remove(organizationId01, projectId01);
        assertFalse(removed);
    }

    @Test
    @Order(39)
    public void testRemoveOrganizations() throws PKIException {
        boolean removed = modelCache.remove(organizationId01);
        assertTrue(removed);

        removed = modelCache.remove(organizationId02);
        assertTrue(removed);

        removed = modelCache.remove(organizationId01);
        assertFalse(removed);

        removed = modelCache.remove(organizationId02);
        assertFalse(removed);
    }

}
