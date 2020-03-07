package itx.iamservice.services;

import itx.iamservice.model.ModelImpl;

public interface PersistenceService {

    void saveModel(ModelImpl model);

    ModelImpl getModel();

}
