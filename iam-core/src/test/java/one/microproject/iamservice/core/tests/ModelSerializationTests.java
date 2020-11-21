package one.microproject.iamservice.core.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientImpl;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.KeyPairData;
import one.microproject.iamservice.core.model.KeyPairSerialized;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.ModelImpl;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.OrganizationImpl;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.ProjectImpl;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.RoleImpl;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserImpl;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPCredentials;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.model.utils.TokenUtils;
import one.microproject.iamservice.core.services.persistence.wrappers.OrganizationWrapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Map<String, String> properties = new HashMap<>();
        properties.put("key", "value");
        Organization organization = new OrganizationImpl(OrganizationId.from("org-001"), "name", Collections.emptyList(), keyPairSerialized, properties);
        String serialized = mapper.writeValueAsString(organization);
        Organization organizationDeserialized = mapper.readValue(serialized, Organization.class);
        assertNotNull(organizationDeserialized);
        assertEquals(OrganizationId.from("org-001"), organizationDeserialized.getId());
        assertEquals("name", organizationDeserialized.getName());
        assertEquals(Collections.emptyList(), organizationDeserialized.getProjects());
        assertEquals(keyPairSerialized.getId(), organizationDeserialized.getKeyPairSerialized().getId());
        assertEquals(keyPairSerialized.getPrivateKey(), organizationDeserialized.getKeyPairSerialized().getPrivateKey());
        assertEquals(keyPairSerialized.getX509Certificate(), organizationDeserialized.getKeyPairSerialized().getX509Certificate());
        assertEquals(organization.getProperties().size(), organizationDeserialized.getProperties().size());
    }

    @Test
    public void serializeAndDeserializeClient() throws JsonProcessingException {
        ClientProperties properties = ClientProperties.from("http://localhost:8080");
        ClientCredentials credentials = new ClientCredentials(ClientId.from("client-001"), "secret");
        Client client = new ClientImpl(credentials, "name", 10L, 10L, Collections.emptyList(), properties);
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
    public void serializeAndDeserializeRole() throws JsonProcessingException {
        Role role = new RoleImpl(RoleId.from("role-001"), "role1", Collections.emptyList());
        String serialized = mapper.writeValueAsString(role);
        Role roleDeserialized = mapper.readValue(serialized, Role.class);
        assertNotNull(roleDeserialized);
        assertEquals(role, roleDeserialized);
    }

    @Test
    public void serializeAndDeserializeUser() throws PKIException, JsonProcessingException {
        Credentials<UPAuthenticationRequest> credentials = new UPCredentials(UserId.from("user"), "secret");
        List<Credentials> credentialsList = new ArrayList<>();
        credentialsList.add(credentials);
        User user = new UserImpl(UserId.from("user-001"), "name",
                ProjectId.from("project-001"), 10L, 10L,
                Collections.emptyList(), credentialsList, keyPairSerialized, "admin@email.com", UserProperties.getDefault());
        String serialized = mapper.writeValueAsString(user);
        User userDeserialized = mapper.readValue(serialized, User.class);
        assertNotNull(userDeserialized);
        assertEquals(user.getId(), userDeserialized.getId());
        assertEquals(user.getName(), userDeserialized.getName());
        assertEquals(user.getProjectId(), userDeserialized.getProjectId());
        assertEquals(user.getDefaultAccessTokenDuration(), userDeserialized.getDefaultAccessTokenDuration());
        assertEquals(user.getDefaultRefreshTokenDuration(), userDeserialized.getDefaultRefreshTokenDuration());
        assertEquals(user.getKeyPairSerialized(), userDeserialized.getKeyPairSerialized());
    }

    @Test
    public void serializeAndDeserializeProject() throws PKIException, JsonProcessingException {
        Map<String, String> properties = new HashMap<>();
        Project project = new ProjectImpl(ProjectId.from("project-001"), "name", OrganizationId.from("organization-001"), keyPairSerialized,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), properties);
        String serialized = mapper.writeValueAsString(project);
        Project projectDeserialized = mapper.readValue(serialized, Project.class);
        assertNotNull(projectDeserialized);
        assertEquals(project.getId(), projectDeserialized.getId());
        assertEquals(project.getName(), projectDeserialized.getName());
        assertEquals(project.getOrganizationId(), projectDeserialized.getOrganizationId());
        assertEquals(project.getKeyPairSerialized(), projectDeserialized.getKeyPairSerialized());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserializeModelKeys() throws JsonProcessingException {
        ModelKey<User> userKey = new ModelKey(User.class, OrganizationId.from("o1"), ProjectId.from("p1"), UserId.from("u1"));
        String serialized = mapper.writeValueAsString(userKey);
        ModelKey<User> userKeyDeserialized = mapper.readValue(serialized, ModelKey.class);
        assertNotNull(userKeyDeserialized);
        assertEquals(userKey, userKeyDeserialized);
        assertEquals(userKey.getType(), userKeyDeserialized.getType());
        assertEquals(userKey.getIds().length, userKeyDeserialized.getIds().length);
        assertEquals(userKey.getIds()[0], userKeyDeserialized.getIds()[0]);
        assertEquals(userKey.getIds()[1], userKeyDeserialized.getIds()[1]);
        assertEquals(userKey.getIds()[2], userKeyDeserialized.getIds()[2]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserializeModelKeyOrganization() throws JsonProcessingException {
        ModelKey<Organization> organizationKey = new ModelKey(Organization.class, OrganizationId.from("o1"));
        String serialized = mapper.writeValueAsString(organizationKey);
        ModelKey<User> userKeyDeserialized = mapper.readValue(serialized, ModelKey.class);
        assertNotNull(userKeyDeserialized);
        assertEquals(organizationKey, userKeyDeserialized);
        assertEquals(organizationKey.getType(), userKeyDeserialized.getType());
        assertEquals(organizationKey.getIds().length, userKeyDeserialized.getIds().length);
        assertEquals(organizationKey.getIds()[0], userKeyDeserialized.getIds()[0]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserializeOrganizationWrapper() throws JsonProcessingException, PKIException {
        ModelKey<Organization> organizationKey = new ModelKey(Organization.class, OrganizationId.from("o1"));
        Organization organization = new OrganizationImpl(OrganizationId.from("o1"), "name");
        OrganizationWrapper wrapper = new OrganizationWrapper(organizationKey, organization);
        String serialized = mapper.writeValueAsString(wrapper);
        OrganizationWrapper deserialized = mapper.readValue(serialized, OrganizationWrapper.class);
        assertNotNull(deserialized);
        Assertions.assertEquals(wrapper.getKey(), deserialized.getKey());
        assertEquals(wrapper.getValue().getId(), deserialized.getValue().getId());
        assertEquals(wrapper.getValue().getName(), deserialized.getValue().getName());

        List<OrganizationWrapper> list = new ArrayList<>();
        list.add(wrapper);
        serialized = mapper.writeValueAsString(list);
        List<OrganizationWrapper> listDeserialized = mapper.readValue(serialized, new TypeReference<List<OrganizationWrapper>>() {});
        assertNotNull(listDeserialized);
        Assertions.assertEquals(list.get(0).getKey(), listDeserialized.get(0).getKey());

    }

    @Test
    public void serializeAndDeserializeUPCredentials() throws PKIException, JsonProcessingException {
        UPAuthenticationRequest validRequest = new UPAuthenticationRequest(UserId.from("user-001"), "secret", null, null);
        UPAuthenticationRequest inValidRequest = new UPAuthenticationRequest(UserId.from("user-001"), "safd", null, null);
        UPCredentials upCredentials = new UPCredentials(UserId.from("user-001"),  "secret");
        String serialized = mapper.writeValueAsString(upCredentials);
        assertTrue(upCredentials.verify(validRequest));
        assertFalse(upCredentials.verify(inValidRequest));

        UPCredentials deserializedCredentials = mapper.readValue(serialized, UPCredentials.class);
        assertTrue(deserializedCredentials.verify(validRequest));
        assertFalse(deserializedCredentials.verify(inValidRequest));
    }

}