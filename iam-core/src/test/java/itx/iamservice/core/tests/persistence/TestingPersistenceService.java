package itx.iamservice.core.tests.persistence;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.keys.ModelKey;
import itx.iamservice.core.services.persistence.PersistenceService;
import itx.iamservice.core.services.persistence.wrappers.ModelWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestingPersistenceService implements PersistenceService {

    private final List<ModelWrapper> modelWrappers;
    private final List<Model> models;
    private final Map<ModelKey<Object>, Object> nodes;
    private final AtomicInteger flushCounter;

    public TestingPersistenceService() {
        this.modelWrappers = new ArrayList<>();
        this.models = new ArrayList<>();
        this.nodes  = new HashMap<>();
        this.flushCounter = new AtomicInteger(0);
    }

    @Override
    public void onModelInitialization(ModelWrapper modelWrapper) {
        modelWrappers.add(modelWrapper);
    }

    @Override
    public void onModelChange(Model model) {
        models.add(model);
    }

    @Override
    public <T> void onNodeCreated(ModelKey<T> modelKey, T newNode) {
        nodes.put((ModelKey<Object>)modelKey, newNode);
    }

    @Override
    public <T> void onNodeUpdated(ModelKey<T> modelKey, T newNode) {
        nodes.put((ModelKey<Object>)modelKey, newNode);
    }

    @Override
    public <T> void onNodeDeleted(ModelKey<T> modelKey, T oldNode) {
        nodes.remove(modelKey);
    }

    @Override
    public void flush() throws Exception {
        flushCounter.incrementAndGet();
    }

    public int getModelWrappersCount() {
        return modelWrappers.size();
    }

    public ModelWrapper getModelWrapper(int index) {
        return modelWrappers.get(index);
    }

    public int getModelsCount() {
        return models.size();
    }

    public Model getModel(int index) {
        return models.get(index);
    }

    public int getNodesCount() {
        return nodes.size();
    }

    public Map<ModelKey<Object>, Object> getNodes() {
        return nodes;
    }

    public int getFlushCount() {
        return flushCounter.get();
    }

    public void reset() {
        modelWrappers.clear();
        models.clear();
        nodes.clear();
        flushCounter.set(0);
    }

}
