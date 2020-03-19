package itx.iamservice.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.services.dto.TokenRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataDeserializationTests {

    @Test
    public void testTokenRequestDeserialization() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = DataDeserializationTests.class.getResourceAsStream("/token-request.json");
        TokenRequest tokenRequest = objectMapper.readValue(inputStream, TokenRequest.class);
        assertNotNull(tokenRequest);
        assertEquals("password", tokenRequest.getGrantType());
        assertEquals("user", tokenRequest.getUsername());
        assertEquals("pwd", tokenRequest.getPassword());
        assertEquals("a b c", tokenRequest.getScope());
        assertEquals("123", tokenRequest.getClientId());
        assertEquals("secret", tokenRequest.getClientSecret());
    }

}
