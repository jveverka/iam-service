package one.microproject.iamservice.examples.performance.tests;


public interface ScenarioFactory<T,R> {

    default TestScenario<T,R> createTestScenario(ResultCache<T, R> cache, int ordinal) throws ScenarioInitException {
        try {
            return createScenario(cache, ordinal);
        } catch (Exception e) {
            throw new ScenarioInitException(e);
        }
    }

    TestScenario<T, R> createScenario(ResultCache<T, R> cache, int ordinal) throws Exception;

}
