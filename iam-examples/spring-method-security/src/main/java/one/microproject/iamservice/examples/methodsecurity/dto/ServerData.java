package one.microproject.iamservice.examples.methodsecurity.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerData {

    private final String data;

    @JsonCreator
    public ServerData(@JsonProperty("data") String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
