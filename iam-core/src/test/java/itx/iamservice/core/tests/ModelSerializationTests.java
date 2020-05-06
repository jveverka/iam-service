package itx.iamservice.core.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.KeyPairSerialized;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.OrganizationImpl;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.utils.TokenUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.Security;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelSerializationTests {

    private static ObjectMapper mapper;
    private static KeyPairSerialized keyPairSerialized;

    @BeforeAll
    public static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
        mapper = new ObjectMapper();
        KeyPairData keyPairData = TokenUtils.createSelfSignedKeyPairData("issuer", 5000L, TimeUnit.SECONDS);
        keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);
    }

    @Test
    public void serializeAndDeserializeModel() throws JsonProcessingException {
        Model model = new ModelImpl(ModelId.from("model-001"), "model1");
        String serialized = mapper.writeValueAsString(model);
        Model modelDeserialized = mapper.readValue(serialized, Model.class);
        assertNotNull(modelDeserialized);
        assertEquals(ModelId.from("model-001"), modelDeserialized.getId());
        assertEquals("model1", modelDeserialized.getName());
    }

    @Test
    public void serializeAndDeserializeOrganization() throws PKIException, JsonProcessingException {
        Organization organization = new OrganizationImpl(OrganizationId.from("org-001"), "name", Collections.emptyList(), keyPairSerialized);
        String serialized = mapper.writeValueAsString(organization);
        Organization organizationDeserialized = mapper.readValue(serialized, Organization.class);
        assertNotNull(organizationDeserialized);
        assertEquals(OrganizationId.from("org-001"), organizationDeserialized.getId());
        assertEquals("name", organizationDeserialized.getName());
        assertEquals(Collections.emptyList(), organizationDeserialized.getProjects());
        assertEquals(keyPairSerialized.getId(), organizationDeserialized.getKeyPairSerialized().getId());
        assertEquals(keyPairSerialized.getPrivateKey(), organizationDeserialized.getKeyPairSerialized().getPrivateKey());
        assertEquals(keyPairSerialized.getX509Certificate(), organizationDeserialized.getKeyPairSerialized().getX509Certificate());
    }

    @Test
    public void serializeAndDeserializeClient() throws PKIException, JsonProcessingException {
        ClientCredentials credentials = new ClientCredentials(ClientId.from("client-001"), "secret");
        Client client = new Client(credentials, "name", 10L, 10L, Collections.emptyList());
        String serialized = mapper.writeValueAsString(client);
        Client clientDeserialized = mapper.readValue(serialized, Client.class);
        assertNotNull(clientDeserialized);
        assertEquals(client.getId(), clientDeserialized.getId());
        assertEquals(client.getName(), clientDeserialized.getName());
        assertEquals(client.getRoles(), clientDeserialized.getRoles());
        assertEquals(client.getCredentials(), clientDeserialized.getCredentials());
        assertEquals(client.getDefaultAccessTokenDuration(), clientDeserialized.getDefaultAccessTokenDuration());
        assertEquals(client.getDefaultRefreshTokenDuration(), clientDeserialized.getDefaultRefreshTokenDuration());
    }

    @Test
    public void serializeAndDeserializePermission() throws JsonProcessingException {
        Permission permission = new Permission("service", "resource", "action");
        String serialized = mapper.writeValueAsString(permission);
        Permission permissionDeserialized = mapper.readValue(serialized, Permission.class);
        assertNotNull(permissionDeserialized);
        assertEquals(permission, permissionDeserialized);
    }

    @Test
    public void serializeAndDeserializeRole() throws PKIException, JsonProcessingException {
        Role role = new Role(RoleId.from("role-001"), "role1", Collections.emptyList());
        String serialized = mapper.writeValueAsString(role);
        Role roleDeserialized = mapper.readValue(serialized, Role.class);
        assertNotNull(roleDeserialized);
        assertEquals(role, roleDeserialized);
    }

}