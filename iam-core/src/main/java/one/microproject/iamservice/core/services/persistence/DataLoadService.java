package one.microproject.iamservice.core.services.persistence;

import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;

/**
 * Service responsible for populating {@link ModelCache} from persistent storage.
 */
public interface DataLoadService {

    ModelWrapper populateCache() throws Exception;

}
