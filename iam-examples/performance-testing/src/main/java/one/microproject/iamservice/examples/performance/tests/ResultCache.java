package one.microproject.iamservice.examples.performance.tests;

import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioResult;

public interface ResultCache<T, R> {

    void onInitFailed(int i);

    void onStarted(ScenarioRequest<T> request);

    void onResult(ScenarioResult<R> result);

}
