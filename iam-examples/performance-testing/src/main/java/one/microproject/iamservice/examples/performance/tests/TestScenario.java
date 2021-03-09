package one.microproject.iamservice.examples.performance.tests;

import one.microproject.iamservice.examples.performance.tests.impl.ScenarioExecException;

/**
 * Implementations of this interface is a Test Scenario. It is executed in single thread.
 * @param <T> - request type, input data into scenario execution.
 * @param <R> - response type, the result of scenario execution.
 */
public interface TestScenario<T, R> {

    /**
     * This method is called only once and represents single scenario run.
     * @param request - input data into scenario execution.
     * @return - the result of scenario execution.
     * @throws ScenarioExecException - thrown in case that scenario execution fails.
     */
    R getResult(T request) throws ScenarioExecException;

}
