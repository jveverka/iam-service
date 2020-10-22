package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IdHolder {

    private final String id;

    @JsonCreator
    public IdHolder(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static IdHolder from(String id) {
        return new IdHolder(id);
    }

}
