package one.microproject.iamservice.examples.performance.tests.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import one.microproject.iamservice.examples.performance.tests.TestScenarioProducer;
import one.microproject.iamservice.examples.performance.tests.dto.RunnerContext;
import one.microproject.iamservice.examples.performance.tests.dto.RunnerResult;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioContext;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioResult;
import org.junit.jupiter.params.provider.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioRunner<T, R> implements ResultCache<T, R> {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioRunner.class);

    private final int runnerIndex;
    private final ExecutorService executorService;
    private final Map<Integer, ScenarioContext<T,R>> scenarios;
    private final TestScenarioProducer<T,R> scenarioProducer;
    private final int nThreads;
    private final int repeat;

    private long started;
    private long duration;

    public ScenarioRunner(int runnerIndex, int nThreads, int repeat, TestScenarioProducer<T,R> scenarioProducer) {
        this.runnerIndex = runnerIndex;
        this.nThreads = nThreads;
        this.repeat = repeat;
        this.executorService = Executors.newFixedThreadPool(nThreads);
        this.scenarios = new ConcurrentHashMap<>();
        this.scenarioProducer = scenarioProducer;
    }

    public void execTests() throws InterruptedException {
        started = System.nanoTime();
        for (int i=0; i<nThreads*repeat; i++) {
            try {
                RunnerContext context = new RunnerContext(runnerIndex, i);
                TestScenarioTask<T, R> testScenarioTask =
                        new TestScenarioTask<>(this, scenarioProducer.createRequest(context), scenarioProducer.createScenario(context));
                executorService.submit(testScenarioTask);
            } catch (ScenarioInitException e) {
                onInitFailed(i, e);
            }
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            LOG.debug("waiting for executor shutdown ...");
        }
        duration = (System.nanoTime() - started)/1000000;
    }

    public Stream<Arguments> getParameters() {
        List<Arguments> argumentsList = new ArrayList<>();
        scenarios.values().forEach(c ->
            argumentsList.add(Arguments.of(c.getScenarioRequest(), c.getScenarioResult()))
        );
        return argumentsList.stream();
    }

    public Collection<ScenarioContext<T,R>> getResults() {
        return scenarios.values();
    }

    public RunnerResult getRunnerResult() {
        return new RunnerResult(runnerIndex, nThreads, repeat,started/1000000, duration);
    }

    @Override
    public void onInitFailed(int i, Throwable t) {
        onStarted(new ScenarioRequest<>(i, null));
        onResult(new ScenarioResult<>(i, false, "INIT ERROR: " + t.getMessage(), System.nanoTime()/1000000, 0L, null));
    }

    @Override
    public void onStarted(ScenarioRequest<T> request) {
        ScenarioContext<T,R> scenarioContext = new ScenarioContext<>(request);
        scenarios.put(request.getId(), scenarioContext);
    }

    @Override
    public void onResult(ScenarioResult<R> result) {
        ScenarioContext<T, R> scenarioContext = scenarios.get(result.getId());
        if (scenarioContext != null) {
            scenarioContext.setScenarioResult(result);
        }
    }

}
