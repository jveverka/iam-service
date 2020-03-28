package itx.iamservice.core.tests;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.impl.persistence.InMemoryPersistenceServiceImpl;
import itx.iamservice.core.services.persistence.PersistenceResult;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.Security;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersistenceTests {

    private static InMemoryPersistenceServiceImpl persistenceService;
    private static String serialized;
    private static Model model;
    private static Model loadedModel;
    private static Organization organization;
    private static Organization loadedOrganization;
    private static Project project;
    private static Project loadedProject;
    private static Client client;
    private static Client loadedClient;
    private static User user;
    private static User loadedUser;


    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
        model = ModelUtils.createDefaultModel("secret");
        persistenceService = new InMemoryPersistenceServiceImpl();
    }

    @Test
    @Order(1)
    public void persistenceSaveTest() throws ExecutionException, InterruptedException {
        Future<PersistenceResult> persistenceResultFuture = persistenceService.saveModel(model);
        assertNotNull(persistenceResultFuture);
        PersistenceResult persistenceResult = persistenceResultFuture.get();
        assertTrue(persistenceResult.isSuccessful());
        serialized = persistenceService.getSerialized(model.getId());
        assertNotNull(serialized);
    }

    @Test
    @Order(2)
    public void persistenceLoadTest() throws ExecutionException, InterruptedException {
        Future<Model> modelFuture = persistenceService.loadModel(model.getId());
        assertNotNull(modelFuture);
        loadedModel = modelFuture.get();
        assertNotNull(loadedModel);
    }

    @Test
    @Order(3)
    public void compareModelsTest() {
        assertEquals(model.getId(), loadedModel.getId());
        assertEquals(model.getName(), loadedModel.getName());
        assertTrue(model.getOrganizations().size() == loadedModel.getOrganizations().size());
    }

    @Test
    @Order(4)
    public void compareOrganizationsInModelTest() {
        Optional<Organization> organizationOptional = model.getOrganizations().stream().filter(o -> ModelUtils.IAM_ADMINS_ORG.equals(o.getId())).findFirst();
        Optional<Organization> loadedOrganizationOptional = loadedModel.getOrganizations().stream().filter(o -> ModelUtils.IAM_ADMINS_ORG.equals(o.getId())).findFirst();
        assertTrue(organizationOptional.isPresent());
        assertTrue(loadedOrganizationOptional.isPresent());

        organization = organizationOptional.get();
        loadedOrganization = loadedOrganizationOptional.get();

        assertEquals(organization.getId(), loadedOrganization.getId());
        assertEquals(organization.getName(), loadedOrganization.getName());
        assertEquals(organization.getKeyPairSerialized(), loadedOrganization.getKeyPairSerialized());
        assertTrue(organization.getProjects().size() == loadedOrganization.getProjects().size());
    }

    @Test
    @Order(5)
    public void compareProjectsInModelTest() {
        Optional<Project> optionalProject = organization.getProjects().stream().filter(p -> ModelUtils.IAM_ADMINS_PROJECT.equals(p.getId())).findFirst();
        Optional<Project> loadedOptionalProject = loadedOrganization.getProjects().stream().filter(p -> ModelUtils.IAM_ADMINS_PROJECT.equals(p.getId())).findFirst();
        assertTrue(optionalProject.isPresent());
        assertTrue(loadedOptionalProject.isPresent());

        project = optionalProject.get();
        loadedProject = loadedOptionalProject.get();

        assertEquals(project.getId(), loadedProject.getId());
        assertEquals(project.getName(), loadedProject.getName());
        assertEquals(project.getPrivateKey(), loadedProject.getPrivateKey());
        assertEquals(project.getCertificate(), loadedProject.getCertificate());
        assertEquals(project.getOrganizationId(), loadedProject.getOrganizationId());
        assertEquals(project.getKeyPairSerialized(), loadedProject.getKeyPairSerialized());
        assertTrue(project.getClients().size() == loadedProject.getClients().size());
        assertTrue(project.getRoles().size() == loadedProject.getRoles().size());
        assertTrue(project.getPermissions().size() == loadedProject.getPermissions().size());
        assertTrue(project.getUsers().size() == loadedProject.getUsers().size());
    }

    @Test
    @Order(6)
    public void compareClientsInModelTest() {
        Optional<Client> optionalClient = project.getClients().stream().filter(c -> ModelUtils.IAM_ADMIN_CLIENT_ID.equals(c.getId())).findFirst();
        Optional<Client> loadedOptionalClient = loadedProject.getClients().stream().filter(c -> ModelUtils.IAM_ADMIN_CLIENT_ID.equals(c.getId())).findFirst();
        assertTrue(optionalClient.isPresent());
        assertTrue(loadedOptionalClient.isPresent());

        client = optionalClient.get();
        loadedClient = loadedOptionalClient.get();

        assertEquals(client.getId(), loadedClient.getId());
        assertEquals(client.getName(), loadedClient.getName());
        assertEquals(client.getDefaultAccessTokenDuration(), loadedClient.getDefaultAccessTokenDuration());
        assertEquals(client.getDefaultRefreshTokenDuration(), loadedClient.getDefaultRefreshTokenDuration());
    }

    @Test
    @Order(7)
    public void compareUsersInModelTest() {
        Optional<User> optionalUser = project.getUsers().stream().filter(c -> ModelUtils.IAM_ADMIN_USER.equals(c.getId())).findFirst();
        Optional<User> loadedOptionalUser = loadedProject.getUsers().stream().filter(c -> ModelUtils.IAM_ADMIN_USER.equals(c.getId())).findFirst();
        assertTrue(optionalUser.isPresent());
        assertTrue(loadedOptionalUser.isPresent());

        user = optionalUser.get();
        loadedUser = loadedOptionalUser.get();
        assertEquals(user.getId(), loadedUser.getId());
        assertEquals(user.getName(), loadedUser.getName());
    }

}