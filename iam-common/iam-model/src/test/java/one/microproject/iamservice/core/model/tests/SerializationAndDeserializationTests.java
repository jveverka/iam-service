package one.microproject.iamservice.core.model.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.model.RoleId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationAndDeserializationTests {

    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRoleIdSerializationAndDeserialization() throws JsonProcessingException {
        RoleId roleId = RoleId.from("role-001");
        String jsonData = mapper.writeValueAsString(roleId);
        RoleId roleIdDeserialized = mapper.readValue(jsonData, RoleId.class);
        assertEquals(roleId, roleIdDeserialized);
    }

}
