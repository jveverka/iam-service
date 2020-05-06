package itx.iamservice.core.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.KeyPairId;
import itx.iamservice.core.model.KeyPairSerialized;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.OrganizationImpl;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectImpl;
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
    public void serializeAndDeserializeProject() throws PKIException, JsonProcessingException {

    }

}