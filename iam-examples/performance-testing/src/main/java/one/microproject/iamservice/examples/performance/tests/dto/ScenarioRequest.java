package one.microproject.iamservice.examples.performance.tests.dto;

public class ScenarioRequest<T> {

    private final Integer id;
    private final T request;

    public ScenarioRequest(Integer id, T request) {
        this.id = id;
        this.request = request;
    }

    public Integer getId() {
        return id;
    }

    public T getRequest() {
        return request;
    }

}
