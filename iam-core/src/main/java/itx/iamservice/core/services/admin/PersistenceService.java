package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.ModelImpl;

public interface PersistenceService {

    void saveModel(ModelImpl model);

    ModelImpl loadModel();

}
