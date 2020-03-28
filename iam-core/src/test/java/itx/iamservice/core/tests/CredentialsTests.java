package itx.iamservice.core.tests;

import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CredentialsTests {

    @Test
    public void testClientCredentials() {
        ClientCredentials clientCredentials01a = new ClientCredentials(ClientId.from("id-01"), "secret-01");
        ClientCredentials clientCredentials02a = new ClientCredentials(ClientId.from("id-02"), "secret-02");
        ClientCredentials clientCredentials01b = new ClientCredentials(ClientId.from("id-01"), "secret-01");
        ClientCredentials clientCredentials02b = new ClientCredentials(ClientId.from("id-02"), "secret-02xx");
        ClientCredentials clientCredentials03a = new ClientCredentials(ClientId.from("id-03"), "secret-02");
        assertEquals(clientCredentials01a, clientCredentials01b);
        assertEquals(clientCredentials01b, clientCredentials01a);
        assertEquals(clientCredentials01a, clientCredentials01a);
        assertEquals(clientCredentials01b, clientCredentials01b);
        assertNotEquals(clientCredentials01a, clientCredentials02a);
        assertNotEquals(clientCredentials02a, clientCredentials01a);
        assertEquals(clientCredentials02a, clientCredentials02a);
        assertNotEquals(clientCredentials02a, clientCredentials02b);
        assertNotEquals(clientCredentials02a, clientCredentials03a);
    }

    @Test
    public void testUsernamePasswordCredentials() throws PKIException {
        UPCredentials upCredentials = new UPCredentials(UserId.from("user-001"), "s3cr3t");
        UPAuthenticationRequest upAuthenticationRequest = new UPAuthenticationRequest(UserId.from("user-001"), "s3cr3t", null, null);
        assertTrue(upCredentials.verify(upAuthenticationRequest));
        upAuthenticationRequest = new UPAuthenticationRequest(UserId.from("user-001a"), "s3cr3t", null, null);
        assertFalse(upCredentials.verify(upAuthenticationRequest));
        upAuthenticationRequest = new UPAuthenticationRequest(UserId.from("user-001"), "secret", null, null);
        assertFalse(upCredentials.verify(upAuthenticationRequest));
    }

}
