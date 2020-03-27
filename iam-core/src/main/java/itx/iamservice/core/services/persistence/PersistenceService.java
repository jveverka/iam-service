package itx.iamservice.core.services.persistence;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;

import java.util.concurrent.Future;

/**
 * Service responsible for persisting internal object model.
 */
public interface PersistenceService {

    /**
     * Submit {@link Model} for persistence. Persistence task will be done on different thread.
     * @param model model to persist.
     * @return future persistence result.
     */
    Future<PersistenceResult> saveModel(Model model);

    /**
     * Load model from persistent storage bu model ID.
     * @param id unique model id.
     * @return {@link Model} or error id model loading fails.
     */
    Future<Model> loadModel(ModelId id);

}
