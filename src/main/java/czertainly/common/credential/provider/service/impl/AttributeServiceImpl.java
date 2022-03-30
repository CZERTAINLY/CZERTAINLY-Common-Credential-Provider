package czertainly.common.credential.provider.service.impl;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.connector.AttributesController;
import com.czertainly.api.model.common.AttributeDefinition;
import com.czertainly.api.model.common.BaseAttributeDefinitionTypes;
import com.czertainly.api.model.common.RequestAttributeDto;
import com.czertainly.core.util.AttributeDefinitionUtils;
import czertainly.common.credential.provider.service.AttributeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AttributeServiceImpl implements AttributeService {
    private static final Logger logger = LoggerFactory.getLogger(AttributesController.class);

    // Soft key store attribute names
    public static final String ATTRIBUTE_KEYSTORE_TYPE = "keyStoreType";
    public static final String ATTRIBUTE_KEYSTORE = "keyStore";
    public static final String ATTRIBUTE_KEYSTORE_PASSWORD = "keyStorePassword";
    public static final String ATTRIBUTE_TRUSTSTORE_TYPE = "trustStoreType";
    public static final String ATTRIBUTE_TRUSTSTORE = "trustStore";
    public static final String ATTRIBUTE_TRUSTSTORE_PASSWORD = "trustStorePassword";
    
    // Soft key store attribute labels
    public static final String ATTRIBUTE_KEYSTORE_TYPE_LABEL = "Key Store Type";
    public static final String ATTRIBUTE_KEYSTORE_LABEL = "Key Store";
    public static final String ATTRIBUTE_KEYSTORE_PASSWORD_LABEL = "Key Store Password";
    public static final String ATTRIBUTE_TRUSTSTORE_TYPE_LABEL = "Trust Store Type";
    public static final String ATTRIBUTE_TRUSTSTORE_LABEL = "Trust Store";
    public static final String ATTRIBUTE_TRUSTSTORE_PASSWORD_LABEL = "Trust Store Password";

    // Basic auth attribute names
    public static final String ATTRIBUTE_USERNAME = "username";
    public static final String ATTRIBUTE_PASSWORD = "password";
    
    // Basic auth attribute names
    public static final String ATTRIBUTE_USERNAME_LABEL = "Username";
    public static final String ATTRIBUTE_PASSWORD_LABEL = "Password";

    // API Key attribute names
    public static final String ATTRIBUTE_APIKEY = "apiKey";
    public static final String ATTRIBUTE_APIKEY_LABEL = "API Key";

    @Value("${keyStore.supportedTypes}")
    private ArrayList<String> supportedKeyStoreTypes;

    @Override
    public List<AttributeDefinition> getAttributes(String kind) {
        // TODO: kinds should be defined as constants, all Pascal Case
        switch (kind) {
            case "SoftKeyStore":
                return getSofKeyStoreAttributes();
            case "Basic":
                return getBasicAttributes();
            case "ApiKey":
                return getApiKeyAttributes();
            default:
                throw new ValidationException(ValidationError.create("Unsupported credential kind {}", kind));
        }
    }

    @Override
    public boolean validateAttributes(String kind, List<RequestAttributeDto> attributes) {
        switch (kind) {
            // TODO: kinds should be defined as constants, all Pascal Case
            case "SoftKeyStore":
                return validateSofKeyStoreAttributes(attributes);
            case "Basic":
                return validateBasicAttributes(attributes);
            case "ApiKey":
                return validateApiKeyAttributes(attributes);
            default:
                throw new ValidationException(ValidationError.create("Unsupported credential kind {}", kind));
        }
    }

    private List<AttributeDefinition> getSofKeyStoreAttributes() {
        List<AttributeDefinition> attrs = new ArrayList<>();

        AttributeDefinition keyStoreType = new AttributeDefinition();
        keyStoreType.setUuid("e334e055-900e-43f1-aedc-54e837028de0");
        keyStoreType.setName(ATTRIBUTE_KEYSTORE_TYPE);
        keyStoreType.setLabel(ATTRIBUTE_KEYSTORE_TYPE_LABEL);
        keyStoreType.setType(BaseAttributeDefinitionTypes.LIST);
        keyStoreType.setRequired(true);
        keyStoreType.setReadOnly(false);
        keyStoreType.setVisible(true);
        keyStoreType.setValue(supportedKeyStoreTypes);
        attrs.add(keyStoreType);

        AttributeDefinition keyStore = new AttributeDefinition();
        keyStore.setUuid("6df7ace9-c501-4d58-953c-f8d53d4fb378");
        keyStore.setName(ATTRIBUTE_KEYSTORE);
        keyStore.setLabel(ATTRIBUTE_KEYSTORE_LABEL);
        keyStore.setType(BaseAttributeDefinitionTypes.FILE);
        keyStore.setRequired(true);
        keyStore.setReadOnly(false);
        keyStore.setVisible(true);
        attrs.add(keyStore);

        AttributeDefinition keyStorePassword = new AttributeDefinition();
        keyStorePassword.setUuid("d975fe42-9d09-4740-a362-fc26f98e55ea");
        keyStorePassword.setLabel(ATTRIBUTE_KEYSTORE_PASSWORD_LABEL);
        keyStorePassword.setName(ATTRIBUTE_KEYSTORE_PASSWORD);
        keyStorePassword.setType(BaseAttributeDefinitionTypes.SECRET);
        keyStorePassword.setRequired(true);
        keyStorePassword.setReadOnly(false);
        keyStorePassword.setVisible(true);
        attrs.add(keyStorePassword);

        AttributeDefinition trustStoreType = new AttributeDefinition();
        trustStoreType.setUuid("c4454807-805a-44e2-81d1-94b56e993786");
        trustStoreType.setName(ATTRIBUTE_TRUSTSTORE_TYPE);
        trustStoreType.setLabel(ATTRIBUTE_TRUSTSTORE_TYPE_LABEL);
        trustStoreType.setType(BaseAttributeDefinitionTypes.LIST);
        trustStoreType.setRequired(false);
        trustStoreType.setReadOnly(false);
        trustStoreType.setVisible(true);
        trustStoreType.setValue(supportedKeyStoreTypes);
        attrs.add(trustStoreType);

        AttributeDefinition trustStore = new AttributeDefinition();
        trustStore.setUuid("6a245220-eaf4-44cb-9079-2228ad9264f5");
        trustStore.setName(ATTRIBUTE_TRUSTSTORE);
        trustStore.setLabel(ATTRIBUTE_TRUSTSTORE_LABEL);
        trustStore.setType(BaseAttributeDefinitionTypes.FILE);
        trustStore.setRequired(false);
        trustStore.setReadOnly(false);
        trustStore.setVisible(true);
        attrs.add(trustStore);

        AttributeDefinition trustStorePassword = new AttributeDefinition();
        trustStorePassword.setUuid("85a874da-1413-4770-9830-4188a37c95ee");
        trustStorePassword.setName(ATTRIBUTE_TRUSTSTORE_PASSWORD);
        trustStorePassword.setLabel(ATTRIBUTE_TRUSTSTORE_PASSWORD_LABEL);
        trustStorePassword.setType(BaseAttributeDefinitionTypes.SECRET);
        trustStorePassword.setRequired(false);
        trustStorePassword.setReadOnly(false);
        trustStorePassword.setVisible(true);
        attrs.add(trustStorePassword);

        return attrs;
    }

    private boolean validateSofKeyStoreAttributes(List<RequestAttributeDto> attributes) {
        AttributeDefinitionUtils.validateAttributes(getSofKeyStoreAttributes(), attributes);

        try {
            String keyStoreBase64 = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_KEYSTORE, attributes);
            byte[] keyStoreBytes = Base64.getDecoder().decode(keyStoreBase64);

            String keyStoreType = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_KEYSTORE_TYPE, attributes);
            String keyStorePassword = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_KEYSTORE_PASSWORD, attributes);

            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(new ByteArrayInputStream(keyStoreBytes), keyStorePassword.toCharArray());
            logger.info("Key store attribute successfully validated. Given key store contains: {}", keyStore.aliases());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException(ValidationError.create(e.getMessage()));
        }

        try {
            String trustStoreBase64 = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_TRUSTSTORE, attributes);
            String trustStoreType = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_TRUSTSTORE_TYPE, attributes);
            String trustStorePassword = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_TRUSTSTORE_PASSWORD, attributes);

            if (!StringUtils.isAnyBlank(trustStoreBase64, trustStoreType, trustStorePassword)) {
                byte[] trustStoreBytes = Base64.getDecoder().decode(trustStoreBase64);
                KeyStore trustStore = KeyStore.getInstance(trustStoreType);
                trustStore.load(new ByteArrayInputStream(trustStoreBytes), trustStorePassword.toCharArray());
                logger.info("Trust store attribute successfully validated. Given trust store contains: {}", trustStore.aliases());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException(ValidationError.create(e.getMessage()));
        }

        return true;
    }

    private List<AttributeDefinition> getBasicAttributes() {
        List<AttributeDefinition> attrs = new ArrayList<>();

        AttributeDefinition username = new AttributeDefinition();
        username.setUuid("fe2d6d35-fb3d-4ea0-9f0b-7e39be93beeb");
        username.setName(ATTRIBUTE_USERNAME);
        username.setLabel(ATTRIBUTE_USERNAME_LABEL);
        username.setType(BaseAttributeDefinitionTypes.STRING);
        username.setRequired(true);
        username.setReadOnly(false);
        username.setVisible(true);
        attrs.add(username);

        AttributeDefinition password = new AttributeDefinition();
        password.setUuid("04506d45-c865-4ddc-b6fc-117ee5d5c8e7");
        password.setName(ATTRIBUTE_PASSWORD);
        password.setLabel(ATTRIBUTE_PASSWORD_LABEL);
        password.setType(BaseAttributeDefinitionTypes.SECRET);
        password.setRequired(true);
        password.setReadOnly(false);
        password.setVisible(true);
        attrs.add(password);

        return attrs;
    }

    private boolean validateBasicAttributes(List<RequestAttributeDto> attributes) {
        AttributeDefinitionUtils.validateAttributes(getBasicAttributes(), attributes);
        return true;
    }

    private List<AttributeDefinition> getApiKeyAttributes() {
        List<AttributeDefinition> attrs = new ArrayList<>();

        AttributeDefinition apiKey = new AttributeDefinition();
        apiKey.setUuid("aac5c2d5-5dc3-4ddb-9dfa-3d76b99135f8");
        apiKey.setName(ATTRIBUTE_APIKEY);
        apiKey.setLabel(ATTRIBUTE_APIKEY_LABEL);
        apiKey.setType(BaseAttributeDefinitionTypes.SECRET);
        apiKey.setRequired(true);
        apiKey.setReadOnly(false);
        apiKey.setVisible(true);
        attrs.add(apiKey);

        return attrs;
    }

    private boolean validateApiKeyAttributes(List<RequestAttributeDto> attributes) {
        AttributeDefinitionUtils.validateAttributes(getApiKeyAttributes(), attributes);
        return true;
    }
}
