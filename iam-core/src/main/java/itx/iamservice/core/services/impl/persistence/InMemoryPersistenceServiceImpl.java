package itx.iamservice.core.services.impl.persistence;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In-memory persistence service implementation (test-purposes) mainly.
 */
public class InMemoryPersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryPersistenceServiceImpl.class);

    @Override
    public void onModelChange(Model model) {
        LOG.info("onModelChange: {}", model.getId());
    }

    @Override
    public <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        LOG.info("onNodeCreated: {}", modelKey);
    }

    @Override
    public <T> void onNodeUpdated(ModelKey<T> modelKey, T oldNode, T newNode) {
        LOG.info("onNodeUpdated: {}", modelKey);
    }

    @Override
    public <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode) {
        LOG.info("onNodeDeleted: {}", modelKey);
    }

}
