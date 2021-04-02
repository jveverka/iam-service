package one.microproject.iamservice.persistence.filesystem;

import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapperImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;

public class TestMain {

    private static final Logger LOG = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Path dataFilePath = Paths.get(args[0]);
        long timeStamp = System.nanoTime();
        PersistenceService persistenceService = new FileSystemPersistenceServiceImpl(dataFilePath);
        ModelWrapper modelWrapper = new ModelWrapperImpl(ModelUtils.DEFAULT_MODEL, persistenceService, false);
        ModelUtils.createModel(3, 3, 4, 100, 5, 3, modelWrapper);
        LOG.info("model create time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        timeStamp = System.nanoTime();
        modelWrapper.flush();
        LOG.info("model flush time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
        DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(dataFilePath);
        timeStamp = System.nanoTime();
        dataLoadService.populateCache();
        LOG.info("model load time: {} ms", ((System.nanoTime() - timeStamp)/1_000_000F));
    }

}
