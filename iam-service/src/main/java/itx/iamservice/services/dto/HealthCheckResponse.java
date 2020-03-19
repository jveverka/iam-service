package itx.iamservice.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthCheckResponse {

    private final String status;
    private final String version;
    private final Long timestamp;

    @JsonCreator
    public HealthCheckResponse(@JsonProperty("status") String status,
                               @JsonProperty("version") String version,
                               @JsonProperty("timestamp") Long timestamp) {
        this.status = status;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public Long getTimestamp() {
        return timestamp;
    }

}
