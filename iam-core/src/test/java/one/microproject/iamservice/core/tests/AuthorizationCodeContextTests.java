package one.microproject.iamservice.core.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.dto.Scope;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthorizationCodeContextTests {

    @Test
    public void serializationAndDeserialization() throws URISyntaxException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AuthorizationCodeContext authCodeContext = new AuthorizationCodeContext(
                new URI("http://localhost:8080"), OrganizationId.from("org-001"), ProjectId.from("proj-001"),
                ClientId.from("cl-01"), UserId.from("usr-01"), "xxx", new Date(), Scope.empty(), Set.of(), "");
        String data = mapper.writeValueAsString(authCodeContext);
        assertNotNull(data);
        AuthorizationCodeContext deserialized = mapper.readValue(data, AuthorizationCodeContext.class);
        assertNotNull(deserialized);
        assertEquals(authCodeContext.getAudience(), deserialized.getAudience());
        assertEquals(authCodeContext.getOrganizationId(), deserialized.getOrganizationId());
        assertEquals(authCodeContext.getProjectId(), deserialized.getProjectId());
        assertEquals(authCodeContext.getClientId(), deserialized.getClientId());
        assertEquals(authCodeContext.getUserId(), deserialized.getUserId());
        assertEquals(authCodeContext.getIssued(), deserialized.getIssued());
        assertEquals(authCodeContext.getIssuerUri(), deserialized.getIssuerUri());
        assertEquals(authCodeContext.getScope(), deserialized.getScope());
        assertEquals(authCodeContext.getState(), deserialized.getState());
    }

}
