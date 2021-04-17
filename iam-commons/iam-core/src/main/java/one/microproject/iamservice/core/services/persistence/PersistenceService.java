package one.microproject.iamservice.core.services.persistence;

import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;

import java.io.IOException;


/**
 * Service responsible for persisting internal object model into persistent Storage.
 */
public interface PersistenceService {

    void onModelChange(ModelWrapper modelWrapper) throws IOException;

}
