package ch.cern.cmms.eamlightejb.data;

import ch.cern.cmms.eamlightejb.tools.Tools;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ApplicationData {

    public static final Integer AUTOCOMPLETE_RESULT_SIZE = 10;

    //
    // BASIC SETTINGS
    //
    public String getTenant() {return Tools.getVariableValue("EAMLIGHT_INFOR_TENANT"); }

    public String getDefaultOrganization() { return Tools.getVariableValue("EAMLIGHT_INFOR_ORGANIZATION"); }

    public String getInforWSURL() {return Tools.getVariableValue("EAMLIGHT_INFOR_WS_URL"); }

    public String getRESTURL() {return getInforWSURL().replace("/axis/services/EWSConnector", "/axis/restservices");}

    public String getAuthenticationMode() { return Tools.getVariableValue("EAMLIGHT_AUTHENTICATION_MODE"); }

    public String getDefaultUser() { return Tools.getVariableValue("EAMLIGHT_DEFAULT_USER"); }

    //
    // ADMIN CREDENTIALS
    //
    public String getAdminPassword() { return Tools.getVariableValue("EAMLIGHT_ADMIN_PASSWORD"); }

    public String getAdminUser() { return Tools.getVariableValue("EAMLIGHT_ADMIN_USER"); }

    public String getLDAPServer() { return Tools.getVariableValue("EAMLIGHT_LDAP_SERVER"); }

    public Integer getLDAPPort() { return Tools.getVariableIntegerValue("EAMLIGHT_LDAP_PORT"); }

    public Boolean trustAllCertificates() { return "true".equalsIgnoreCase(Tools.getVariableValue("EAMLIGHT_TRUST_ALL_CERTIFICATES")); }

    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }
}
