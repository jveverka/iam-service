package itx.iamservice.persistence;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.services.persistence.PersistenceService;

import java.util.concurrent.Future;

public class PersistenceServiceImpl implements PersistenceService {

    @Override
    public Future<Void> saveModel(Model model) {
        return null;
    }

    @Override
    public Future<Model> loadModel(ModelId id) {
        return null;
    }

}
