package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfoResponse {

    private final String sub;

    @JsonCreator
    public UserInfoResponse(@JsonProperty("sub") String sub) {
        this.sub = sub;
    }

    public String getSub() {
        return sub;
    }

}
