package itx.examples.webflux.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData {

    private final String id;
    private final String email;
    private final String name;

    @JsonCreator
    public UserData(@JsonProperty("id") String id,
                    @JsonProperty("email") String email,
                    @JsonProperty("name") String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
