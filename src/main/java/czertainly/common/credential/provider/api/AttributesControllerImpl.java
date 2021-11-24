package czertainly.common.credential.provider.api;

import java.util.List;

import czertainly.common.credential.provider.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.AttributesController;
import com.czertainly.api.model.AttributeDefinition;

@RestController
@RequestMapping("/v1/credentialProvider/{kind}/attributes")
public class AttributesControllerImpl implements AttributesController {

    @Autowired
    private AttributeService attributeService;

    @Override
    public List<AttributeDefinition> listAttributeDefinitions(@PathVariable String kind) {
        return attributeService.getAttributes(kind);
    }

    @Override
    public boolean validateAttributes(@PathVariable String kind, @RequestBody List<AttributeDefinition> attributes) throws ValidationException {
        return attributeService.validateAttributes(kind, attributes);
    }
}
