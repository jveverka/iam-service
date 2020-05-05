package itx.iamservice.core.services.persistence;

import itx.iamservice.core.model.keys.ModelKey;


/**
 * Service responsible for persisting internal object model.
 */
public interface PersistenceService {

    <T> void onNodeCreated(ModelKey<T> modelKey, T newNode);

    <T> void onNodeUpdated(ModelKey<T> modelKey, T oldNode, T newNode);

    <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode);

}
