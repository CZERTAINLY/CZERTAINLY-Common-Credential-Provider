package czertainly.common.credential.provider.util;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.AttributeDefinition;
import com.czertainly.api.model.NameAndIdDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttributeDefinitionUtils {

    private static final ObjectMapper ATTRIBUTES_OBJECT_MAPPER = new ObjectMapper();

    public static AttributeDefinition getAttributeDefinition(String name, List<AttributeDefinition> attributes) {
        return attributes.stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }

    public static boolean containsAttributeDefinition(String name, List<AttributeDefinition> attributes) {
        AttributeDefinition definition = getAttributeDefinition(name, attributes);
        return definition != null;
    }

    public static <T extends Object> T getAttributeValue(String name, List<AttributeDefinition> attributes) {
        AttributeDefinition definition = getAttributeDefinition(name, attributes);

        if (definition == null || definition.getValue() == null) {
            return null;
        }

        return (T) definition.getValue();
    }

    public static NameAndIdDto getNameAndIdValue(String name, List<AttributeDefinition> attributes) {
        Serializable value = getAttributeValue(name, attributes);

        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Could not get NameAndId value. Attribute has wrong value: " + value);
        }

        Map<String, ?> valueMap = (Map) value;
        if (valueMap.containsKey("id") && valueMap.containsKey("name")) {
            return new NameAndIdDto(
                    (int) valueMap.get("id"),
                    (String) valueMap.get("name"));
        } else {
            throw new IllegalArgumentException("Could not get NameAndId value. Attribute has wrong value: " + value);
        }
    }

    public static String serialize(List<AttributeDefinition> attributes) {
        if (attributes == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.writeValueAsString(attributes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<AttributeDefinition> deserialize(String attributesJson) {
        if (attributesJson == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.readValue(attributesJson, new TypeReference<List<AttributeDefinition>>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void validateAttributes(List<AttributeDefinition> definitions, List<AttributeDefinition> attributes) throws ValidationException {
        List<ValidationError> errors = new ArrayList<>();

        for (AttributeDefinition definition : definitions) {
            Serializable value = getAttributeValue(definition.getName(), attributes);

            if (definition.isRequired() && value == null) {
                errors.add(ValidationError.create("Value of required field {} not found.", definition.getName()));
                continue;
            }

            if (value instanceof String && definition.getValidationRegex() != null) {
                Pattern pattern;
                try {
                    pattern = Pattern.compile(definition.getValidationRegex());
                    Matcher matcher = pattern.matcher((String) value);
                    if (!matcher.matches()) {
                        errors.add(ValidationError.create(
                                "Value {} of attribute {} doesn't match regex {}",
                                value,
                                definition.getName(),
                                definition.getValidationRegex()));
                    }
                } catch (Exception e) {
                    errors.add(ValidationError.create(
                            "Could not validate value of field {} due to error {}",
                            definition.getName(),
                            ExceptionUtils.getRootCauseMessage(e)));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Attributes validation failed.", errors);
        }
    }

    public static List<AttributeDefinition> createAttributes(String name, Serializable value) {
        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setName(name);
        attribute.setValue(value);
        return createAttributes(attribute);
    }

    public static List<AttributeDefinition> createAttributes(AttributeDefinition attribute) {
        List<AttributeDefinition> attributes = createAttributes();
        attributes.add(attribute);

        return attributes;
    }

    public static List<AttributeDefinition> createAttributes() {
        return new ArrayList<>();
    }
}

