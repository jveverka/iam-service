package one.microproject.iamservice.core.services.persistence;

import one.microproject.iamservice.core.services.caches.ModelCache;

/**
 * Service responsible for populating {@link ModelCache} from persistent storage.
 */
public interface DataLoadService {

    ModelCache populateCache() throws Exception;

}
