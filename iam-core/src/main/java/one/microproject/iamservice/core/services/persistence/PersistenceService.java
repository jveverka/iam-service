package one.microproject.iamservice.core.services.persistence;

import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.keys.ModelKey;


/**
 * Service responsible for persisting internal object model into persistent Storage.
 */
public interface PersistenceService {

    void onModelChange(Model model);

    <T> void onNodeCreated(ModelKey<T> modelKey, T newNode);

    <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode);

    void flush() throws Exception;

}
