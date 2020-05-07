package itx.iamservice.core.services.persistence;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;


/**
 * Service responsible for persisting internal object model into persistent Storage.
 */
public interface PersistenceService {

    void onModelInitialization(ModelWrapper modelWrapper);

    void onModelChange(Model model);

    <T> void onNodeCreated(ModelKey<T> modelKey, T newNode);

    <T> void onNodeUpdated(ModelKey<T> modelKey, T newNode);

    <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode);

    void flush() throws Exception;

}
