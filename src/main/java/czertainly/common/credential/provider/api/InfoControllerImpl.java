package czertainly.common.credential.provider.api;

import java.util.ArrayList;
import java.util.List;

import com.czertainly.api.interfaces.InfoController;
import com.czertainly.api.model.connector.FunctionGroupCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.czertainly.api.model.connector.InfoResponse;
import czertainly.common.credential.provider.EndpointsListener;

@RestController
@RequestMapping("/v1")
public class InfoControllerImpl implements InfoController {
    private static final Logger logger = LoggerFactory.getLogger(InfoControllerImpl.class);

    @Autowired
    private EndpointsListener endpointsListener;

    private static List<String> KINDS = List.of("SoftKeyStore", "Basic", "ApiKey");

    @Override
    public List<InfoResponse> listSupportedFunctions() {
        logger.info("Listing the end points for common credential provider");
        List<InfoResponse> functions = new ArrayList<>();
        functions.add(new InfoResponse(KINDS, FunctionGroupCode.CREDENTIAL_PROVIDER, endpointsListener.getEndpoints(FunctionGroupCode.CREDENTIAL_PROVIDER)));
        return functions;
    }
}
