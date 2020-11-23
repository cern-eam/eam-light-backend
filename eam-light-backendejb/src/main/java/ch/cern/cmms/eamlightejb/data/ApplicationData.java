package ch.cern.cmms.eamlightejb.data;

import ch.cern.cmms.eamlightejb.tools.Tools;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@ApplicationScoped
public class ApplicationData {

    public static final Integer AUTOCOMPLETE_RESULT_SIZE = 10;

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
    public String getAdminPassword() { return Tools.getVariableValue("EAMLIGHT_ADMIN_PASSWORD"); }

    public String getAdminUser() { return Tools.getVariableValue("EAMLIGHT_ADMIN_USER"); }

    public String getServiceAccount() { return Tools.getVariableValue("EAMLIGHT_SERVICE_ACCOUNT"); }

    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }
}
