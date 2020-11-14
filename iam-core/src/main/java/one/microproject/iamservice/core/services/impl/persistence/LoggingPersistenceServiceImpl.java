package one.microproject.iamservice.core.services.impl.persistence;

import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * In-memory persistence service implementation (test-purposes) mainly.
 */
public class LoggingPersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingPersistenceServiceImpl.class);

    @Override
    public void onModelChange(ModelWrapper modelWrapper) throws IOException {
        LOG.info("onModelChange: NOOP {}", modelWrapper.getModel().getId());
    }

}
