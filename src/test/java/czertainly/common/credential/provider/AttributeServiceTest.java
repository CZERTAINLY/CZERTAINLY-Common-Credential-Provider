package czertainly.common.credential.provider;

import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttributeDto;
import com.czertainly.api.model.common.attribute.v2.BaseAttribute;
import com.czertainly.api.model.common.attribute.v2.content.SecretAttributeContent;
import com.czertainly.api.model.common.attribute.v2.content.StringAttributeContent;
import com.czertainly.api.model.common.attribute.v2.content.data.SecretAttributeContentData;
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

    private List<RequestAttributeDto> attributesBasic;
    private List<RequestAttributeDto> attributesApiKey;

    @BeforeEach
    public void setup(){
        RequestAttributeDto username = new RequestAttributeDto();
        username.setUuid("1b6c48ad-c1c7-4c82-91ef-3e61bc9f52ac");
        username.setContent(List.of(new StringAttributeContent("admin")));
        username.setName("username");

        RequestAttributeDto password = new RequestAttributeDto();
        password.setUuid("9379ca2c-aa51-42c8-8afd-2a2d16c99c56");
        password.setContent(List.of(new SecretAttributeContent("", new SecretAttributeContentData("admin"))));
        password.setName("password");

        attributesBasic = Arrays.asList(username, password);

        RequestAttributeDto apiKey = new RequestAttributeDto();
        apiKey.setUuid("9379ca2c-aa51-42c8-8afd-2a2d16c99c56");
        apiKey.setContent(List.of(new SecretAttributeContent("", new SecretAttributeContentData("ASufvjhFUtydFDFA"))));
        apiKey.setName("apiKey");

        attributesApiKey = List.of(apiKey);
    }

    @Test
    public void testSoftkeyAttributeResponse() {
        List<BaseAttribute> attributes = attributeService.getAttributes("SoftKeyStore");
        Assertions.assertEquals(6, attributes.size());
    }

    @Test
    public void testBasicAttributeResponse() {
        List<BaseAttribute> attributes = attributeService.getAttributes("Basic");
        Assertions.assertEquals(2, attributes.size());
    }

    @Test
    public void testApiKeyAttributeResponse() {
        List<BaseAttribute> attributes = attributeService.getAttributes("ApiKey");
        Assertions.assertEquals(1, attributes.size());
    }

    @Test
    public void testValidateAttributesBasic() {
        Assertions.assertTrue(attributeService.validateAttributes("Basic", attributesBasic));
    }

    @Test
    public void testValidateAttributesApiKey() {
        Assertions.assertTrue(attributeService.validateAttributes("ApiKey", attributesApiKey));
    }

    @Test
    public void testValidateAttributes_Fail() {
        Assertions.assertThrows(ValidationException.class, () -> attributeService.validateAttributes("default",null));
    }
}
