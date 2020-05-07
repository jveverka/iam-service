package itx.iamservice.core.services.impl.persistence;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;
import itx.iamservice.core.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In-memory persistence service implementation (test-purposes) mainly.
 */
public class LoggingPersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingPersistenceServiceImpl.class);

    @Override
    public void onModelInitialization(ModelWrapper modelWrapper) {
        LOG.info("onModelInitialization: {}", modelWrapper.getModel().getId());
    }

    @Override
    public void onModelChange(Model model) {
        LOG.info("onModelChange: {}", model.getId());
    }

    @Override
    public <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        LOG.info("onNodeCreated: {}", modelKey);
    }

    @Override
    public <T> void onNodeUpdated(ModelKey<T> modelKey, T newNode) {
        LOG.info("onNodeUpdated: {}", modelKey);
    }

    @Override
    public <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode) {
        LOG.info("onNodeDeleted: {}", modelKey);
    }

    @Override
    public void flush() throws Exception {
        LOG.info("flush:");
    }

}
