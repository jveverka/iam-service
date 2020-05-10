package itx.iamservice.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthCheckResponse {

    private final String id;
    private final String type;
    private final String version;
    private final String name;
    private final String status;
    private final Long timestamp;

    @JsonCreator
    public HealthCheckResponse(@JsonProperty("id") String id,
                               @JsonProperty("type") String type,
                               @JsonProperty("status") String status,
                               @JsonProperty("version") String version,
                               @JsonProperty("name") String name,
                               @JsonProperty("timestamp") Long timestamp) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.version = version;
        this.name = name;
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

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
