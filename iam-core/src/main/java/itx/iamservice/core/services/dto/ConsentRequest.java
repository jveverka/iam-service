package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class ConsentRequest {

    private final Code code;
    private final Collection<String> scopes;

    @JsonCreator
    public ConsentRequest(@JsonProperty("code") Code code,
                          @JsonProperty("scopes") Collection<String> scopes) {
        this.code = code;
        this.scopes = scopes;
    }

    public Code getCode() {
        return code;
    }

    public Collection<String> getScopes() {
        return scopes;
    }

}
