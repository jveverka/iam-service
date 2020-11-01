package one.microproject.iamservice.persistence.inmemory;

import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;

public class InMemoryDataLoadServiceImpl implements DataLoadService {

    private final ModelWrapper modelWrapper;
    private final PersistenceService persistenceService;

    public InMemoryDataLoadServiceImpl(ModelWrapper modelWrapper, PersistenceService persistenceService) {
        this.modelWrapper = modelWrapper;
        this.persistenceService = persistenceService;
    }

    @Override
    public ModelCache populateCache() {
        return new ModelCacheImpl(modelWrapper, persistenceService);
    }

}
