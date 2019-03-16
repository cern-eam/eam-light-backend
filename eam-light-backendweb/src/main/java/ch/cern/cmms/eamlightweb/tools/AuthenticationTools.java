package ch.cern.cmms.eamlightweb.tools;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class AuthenticationTools {

    @Inject
    private HttpServletRequest request;
    @Inject
    private InforClient inforClient;
    @Inject
    private ApplicationData applicationData;

    public InforContext getInforContext() throws InforException
    {
        String user = null;
        String password = null;
        String organization = null;
        String sessionid = null;
        String authenticationMode = applicationData.getAuthenticationMode();

        if ("LOCAL".equalsIgnoreCase(authenticationMode)) {
            user = request.getParameter("user");
            if (user == null) {
                user = System.getProperty("DEFAULT_USER").toUpperCase();
            }
            password = applicationData.getPassphrase();
            organization = applicationData.getDefaultOrganization();
        } else if ("SSO".equalsIgnoreCase(authenticationMode)) {
            user = request.getHeader("ADFS_LOGIN").toUpperCase();
            password = applicationData.getPassphrase();
            organization = applicationData.getDefaultOrganization();
        } else {
            user = request.getHeader("INFOR_USER");
            password = request.getHeader("INFOR_PASSWORD");
            organization = request.getHeader("INFOR_ORGANIZATION");
            sessionid = request.getHeader("INFOR_SESSIONID");
        }

        InforContext inforContext = new InforContext();

        // Organization
        if (!notEmpty(organization)) {
            throw inforClient.getTools().generateFault("Organization is required.");
        }
        inforContext.setOrganizationCode(organization);

        // Credentials, Session ID
        if (notEmpty(user) && notEmpty(password)) {
            Credentials credentials = new Credentials();
            credentials.setUsername(user);
            credentials.setPassword(password);
            inforContext.setCredentials(credentials);
        } else if (notEmpty(sessionid)) {
            inforContext.setSessionID(sessionid);
        } else {
            throw inforClient.getTools().generateFault("Credentials or Session ID is required.");
        }

        return inforContext;
    }

    private boolean notEmpty(String string) {
        return string != null && !string.trim().equals("");
    }
}
