package itx.iamservice.core.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelSerializationAndDeserializationTests {

    private static Model model;
    private static ObjectMapper mapper;
    private static String serializedModel;

    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
        model = ModelUtils.createDefaultModel("secret");
        mapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    public void simpleSerializeModelTest() throws JsonProcessingException {
        serializedModel = mapper.writeValueAsString(model);
        assertNotNull(serializedModel);
    }

    @Test
    @Order(2)
    public void simpleDeserializeModelTest() throws IOException {
        Model deserializedMode = mapper.readValue(serializedModel, Model.class);
        assertNotNull(deserializedMode);
    }

}
