package itx.iamservice.core.services;

import itx.iamservice.core.model.ModelImpl;

public interface PersistenceService {

    void saveModel(ModelImpl model);

    ModelImpl getModel();

}
