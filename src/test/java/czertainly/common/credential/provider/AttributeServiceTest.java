package czertainly.common.credential.provider;

import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.AttributeDefinition;
import czertainly.common.credential.provider.service.AttributeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class AttributeServiceTest {
    @Autowired
    private AttributeService attributeService;

    private List<AttributeDefinition> attributesBasic;
    private List<AttributeDefinition> attributesApiKey;

    @BeforeEach
    private void setup(){
        AttributeDefinition username = new AttributeDefinition();
        username.setUuid("1b6c48ad-c1c7-4c82-91ef-3e61bc9f52ac");
        username.setValue("admin");
        username.setName("username");

        AttributeDefinition password = new AttributeDefinition();
        password.setUuid("9379ca2c-aa51-42c8-8afd-2a2d16c99c56");
        password.setValue("admin");
        password.setName("password");

        attributesBasic = Arrays.asList(username, password);

        AttributeDefinition apiKey = new AttributeDefinition();
        apiKey.setUuid("9379ca2c-aa51-42c8-8afd-2a2d16c99c56");
        apiKey.setValue("ASufvjhFUtydFDFA");
        apiKey.setName("apiKey");

        attributesApiKey = Arrays.asList(apiKey);
    }

    @Test
    public void testSoftkeyAttributeResponse() {
        List<AttributeDefinition> attributes = attributeService.getAttributes("SoftKeyStore");
        Assertions.assertEquals(6, attributes.size());
    }

    @Test
    public void testBasicAttributeResponse() {
        List<AttributeDefinition> attributes = attributeService.getAttributes("Basic");
        Assertions.assertEquals(2, attributes.size());
    }

    @Test
    public void testApiKeyAttributeResponse() {
        List<AttributeDefinition> attributes = attributeService.getAttributes("ApiKey");
        Assertions.assertEquals(1, attributes.size());
    }

    @Test
    public void testValidateAttributesBasic() {
        Assertions.assertEquals(true, attributeService.validateAttributes("Basic", attributesBasic));
    }

    @Test
    public void testValidateAttributesApiKey() {
        Assertions.assertEquals(true, attributeService.validateAttributes("ApiKey", attributesApiKey));
    }

    @Test
    public void testValidateAttributes_Fail() {
        Assertions.assertThrows(ValidationException.class, () -> attributeService.validateAttributes("default",null));
    }
}
