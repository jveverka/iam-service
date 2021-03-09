package one.microproject.iamservice.examples.performance.tests.impl;

import one.microproject.iamservice.examples.performance.tests.TestScenario;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioResult;

import java.util.concurrent.Callable;

public class TestScenarioTask<T,R> implements Callable<ScenarioResult<R>> {

    private final ScenarioRequest<T> request;
    private final ResultCache<T,R> resultCache;
    private final TestScenario<T,R> scenario;

    protected TestScenarioTask(ResultCache<T,R> resultCache, ScenarioRequest<T> request, TestScenario<T,R> scenario) {
        this.resultCache = resultCache;
        this.request =  request;
        this.scenario = scenario;
    }

    @Override
    public ScenarioResult<R> call() throws Exception {
        long started = System.nanoTime();
        resultCache.onStarted(request);
        ScenarioResult<R> scenarioResult = null;
        try {
            R result = scenario.getResult(request.getRequest());
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

}
