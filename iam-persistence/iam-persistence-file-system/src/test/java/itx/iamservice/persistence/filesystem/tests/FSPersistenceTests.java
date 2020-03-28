package itx.iamservice.persistence.filesystem.tests;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.persistence.PersistenceResult;
import itx.iamservice.core.services.persistence.PersistenceService;
import itx.iamservice.persistence.filesystem.FileSystemPersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FSPersistenceTests {

    private static Model model;
    private static PersistenceService persistenceService;
    private static Path dataTempDir;

    @BeforeAll
    private static void init() throws PKIException, IOException {
        Security.addProvider(new BouncyCastleProvider());
        model = ModelUtils.createDefaultModel("secret");
        dataTempDir = Files.createTempDirectory("test");
    }

    @Test
    @Order(1)
    public void persistenceSaveTest() throws ExecutionException, InterruptedException {
        persistenceService = new FileSystemPersistenceServiceImpl(dataTempDir);
        Future<PersistenceResult> persistenceResultFuture = persistenceService.saveModel(model);
        assertNotNull(persistenceResultFuture);
        assertTrue(persistenceResultFuture.get().isSuccessful());
    }

    @Test
    @Order(2)
    public void persistenceLoadTest() throws ExecutionException, InterruptedException {
        persistenceService = new FileSystemPersistenceServiceImpl(dataTempDir);
        Future<Model> modelFuture = persistenceService.loadModel(model.getId());
        assertNotNull(modelFuture);
        assertNotNull(modelFuture.get());
    }

    @AfterAll
    private static void shutdown() throws IOException {
        Files.list(dataTempDir).forEach(p->{
            try {
                Files.delete(p);
            } catch (IOException e) {
            }
        });
        Files.delete(dataTempDir);
    }

}