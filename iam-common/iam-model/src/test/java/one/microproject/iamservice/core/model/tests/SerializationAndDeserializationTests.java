package one.microproject.iamservice.core.model.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.dto.ErrorType;
import one.microproject.iamservice.core.dto.TokenResponseError;
import one.microproject.iamservice.core.model.RoleId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializationAndDeserializationTests {

    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    void testRoleIdSerializationAndDeserialization() throws JsonProcessingException {
        RoleId roleId = RoleId.from("role-001");
        String jsonData = mapper.writeValueAsString(roleId);
        RoleId roleIdDeserialized = mapper.readValue(jsonData, RoleId.class);
        assertEquals(roleId, roleIdDeserialized);
    }

    @Test
    void testTokenResponseErrorSerializationAndDeserialization() throws JsonProcessingException {
        TokenResponseError tokenResponseError = TokenResponseError.from(ErrorType.ACCESS_DENIED, "description", "state");
        String jsonData = mapper.writeValueAsString(tokenResponseError);
        TokenResponseError responseDeserialized = mapper.readValue(jsonData, TokenResponseError.class);
        assertEquals(tokenResponseError.getError(), responseDeserialized.getError());
        assertEquals(tokenResponseError.getState(), responseDeserialized.getState());
        assertEquals(tokenResponseError.getErrorDescription(), responseDeserialized.getErrorDescription());
    }

}
