package one.microproject.iamservice.examples.performance.tests.dto;

import java.util.Optional;

public class ScenarioResult<T> {

    private final Integer id;
    private final Boolean success;
    private final String message;
    private final Long started;
    private final Long duration;
    private final T result;

    public ScenarioResult(Integer id, Boolean success, String message, Long started, Long duration, T result) {
        this.id = id;
        this.success = success;
        this.message = message;
        this.started = started;
        this.duration = duration;
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getStarted() {
        return started;
    }

    public Long getDuration() {
        return duration;
    }

    public Optional<T> getResult() {
        return Optional.ofNullable(result);
    }

}
