package itx.iamservice.persistence.inmemory;

import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.impl.caches.ModelCacheImpl;
import itx.iamservice.core.services.persistence.DataLoadService;
import itx.iamservice.core.services.persistence.PersistenceService;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;

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
