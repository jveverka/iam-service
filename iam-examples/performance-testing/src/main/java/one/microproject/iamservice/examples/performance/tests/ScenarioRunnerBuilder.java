package one.microproject.iamservice.examples.performance.tests;

import one.microproject.iamservice.examples.performance.tests.impl.ScenarioRunner;

public class ScenarioRunnerBuilder<T,R> {

    private int runnerIndex = 1;
    private int nThreads = 1;
    private int repeat = 1;
    private TestScenarioProducer<T,R> scenarioProducer;

    ScenarioRunnerBuilder<T,R> setIndex(int runnerIndex) {
        this.runnerIndex = runnerIndex;
        return this;
    }

    ScenarioRunnerBuilder<T,R> setNumberOfThreads(int nThreads) {
        this.nThreads = nThreads;
        return this;
    }

    ScenarioRunnerBuilder<T,R> setRepeat(int repeat) {
        this.repeat = repeat;
        return this;
    }

    ScenarioRunnerBuilder<T,R> withScenarioProducer(TestScenarioProducer<T,R> scenarioProducer) {
        this.scenarioProducer = scenarioProducer;
        return this;
    }

    public ScenarioRunner<T,R> build() {
        return new ScenarioRunner(runnerIndex, nThreads, repeat, scenarioProducer);
    }

}
