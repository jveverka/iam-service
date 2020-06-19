package itx.iamservice.core.services.persistence;

import itx.iamservice.core.services.caches.ModelCache;

/**
 * Service responsible for populating {@link ModelCache} from persistent storage.
 */
public interface DataLoadService {

    ModelCache populateCache() throws Exception;

}
