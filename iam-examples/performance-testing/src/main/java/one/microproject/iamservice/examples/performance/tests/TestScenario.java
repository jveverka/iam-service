package one.microproject.iamservice.examples.performance.tests;

import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioResult;

import java.util.concurrent.Callable;

public abstract class TestScenario<T,R> implements Callable<ScenarioResult<R>> {

    private final ScenarioRequest<T> request;
    private final ResultCache<T,R> resultCache;

    protected TestScenario(ResultCache<T,R> resultCache, ScenarioRequest<T> request) {
        this.request = request;
        this.resultCache = resultCache;
    }

    @Override
    public ScenarioResult<R> call() throws Exception {
        ScenarioResult<R> scenarioResult = null;
        long started = System.nanoTime();
        resultCache.onStarted(request);
        try {
            R result = getResult(request.getRequest());
            long duration = (System.nanoTime() - started)/1000000;
            scenarioResult = new ScenarioResult<>(request.getId(), true, "OK", started/1000000, duration, result);
            resultCache.onResult(scenarioResult);
        } catch (Exception e) {
            long duration = (System.nanoTime() - started)/1000000;
            scenarioResult = new ScenarioResult<>(request.getId(), false, "ERROR", started/1000000, duration, null);
            resultCache.onResult(scenarioResult);
        }
        return scenarioResult;
    }

    public abstract R getResult(T request) throws ScenarioExecException;

}
