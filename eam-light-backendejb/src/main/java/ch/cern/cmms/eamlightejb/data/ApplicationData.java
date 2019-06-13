package ch.cern.cmms.eamlightejb.data;

import ch.cern.cmms.eamlightejb.tools.Tools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static ch.cern.eam.wshub.core.tools.GridTools.getCellContent;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class ApplicationData {

    private Map<String, String> eamlightValues;
    @Inject
    private InforClient inforClient;

    @PostConstruct
    private void init() {
        try {
            GridRequest gridRequest = new GridRequest("BSINST");
            gridRequest.getGridRequestFilters().add(new GridRequestFilter("installcode", "EL_", "BEGINS"));
            Credentials credentials = new Credentials();
            credentials.setUsername(getAdminUser());
            credentials.setPassword(getAdminPassword());
            GridRequestResult result = inforClient.getGridsService().executeQuery(inforClient.getTools().getInforContext(credentials), gridRequest);
            eamlightValues = Arrays.stream(result.getRows()).collect(toMap(row -> getCellContent("installcode", row), row -> getCellContent("value", row)));
        } catch (Exception e) {
            inforClient.getTools().log(Level.SEVERE, "Couldn't fetch application data: " + e.getMessage());
            eamlightValues = new HashMap<>();
        }
    }

    //
    // BASIC SETTINGS
    //
    public String getTenant() {return Tools.getVariableValue("EAMLIGHT_INFOR_TENANT"); }

    public String getDefaultOrganization() { return Tools.getVariableValue("EAMLIGHT_INFOR_ORGANIZATION"); }

    public String getInforWSURL() {return Tools.getVariableValue("EAMLIGHT_INFOR_WS_URL"); }

    public String getAuthenticationMode() { return Tools.getVariableValue("EAMLIGHT_AUTHENTICATION_MODE"); }

    public String getDefaultUser() { return Tools.getVariableValue("EAMLIGHT_DEFAULT_USER"); }

    //
    // ADMIN CREDENTIALS
    //
    public String getAdminPassword() { return Tools.getVariableValue("EAMLIGHT_PASSPHRASE"); }

    public String getAdminUser() { return "R5"; }

    //
    // VALUES
    //
    public Map<String, String> getValues() {
        return eamlightValues;
    }

    public String getValue(String key) { return eamlightValues.get(key); }

    public String[] getCryoEqpReplacementClasses() {
        return eamlightValues.get("EL_EQRPG").replaceAll("\\s+", "").trim().split(",");
    }

}
