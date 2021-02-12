package ch.cern.cmms.eamlightweb.application;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ApplicationService {

    public static final Map<String, String> paramFieldCache = new ConcurrentHashMap<>();
    private static final String SERVICE_ACCOUNTS_PARAM = "EL_SERVI";

    @Inject
    private InforClient inforClient;
    @Inject
    private AuthenticationTools authenticationTools;
    
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, String> getParams() throws InforException {
        if (paramFieldCache.isEmpty()) {
            GridRequest gridRequest = new GridRequest("BSINST");
            gridRequest.addFilter("installcode", "EL_", "BEGINS");
            Map<String, String> paramsMap = GridTools.convertGridResultToMap("installcode", "value",
                    inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
            //paramsMap.put("EAMLIGHT_SERVICE_ACCOUNT", applicationData.getServiceAccount());
            paramFieldCache.putAll(paramsMap);
        }
        return paramFieldCache;
    }

    public Map<String, String> getServiceAccounts() throws InforException {
        try {
            Map<String, String> params = getParams();
            if (!params.containsKey(SERVICE_ACCOUNTS_PARAM)) {
                return new HashMap<>();
            }
            String serviceAccountsParam = params.get(SERVICE_ACCOUNTS_PARAM);
            return mapper.readValue(serviceAccountsParam, Map.class);
        } catch (InforException | JsonProcessingException e) {
            e.printStackTrace();
            throw new InforException("Could not read allowed service accounts", null, null);
        }
    }
}