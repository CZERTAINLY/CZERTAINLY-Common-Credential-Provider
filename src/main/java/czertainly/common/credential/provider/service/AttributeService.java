package czertainly.common.credential.provider.service;

import java.util.List;

import com.czertainly.api.model.AttributeDefinition;

public interface AttributeService {
	List<AttributeDefinition> getAttributes(String kind);
	
	boolean validateAttributes(String kind, List<AttributeDefinition> attributes);
}
