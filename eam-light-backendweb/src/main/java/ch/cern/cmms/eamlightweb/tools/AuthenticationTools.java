package ch.cern.cmms.eamlightweb.tools;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.tools.InforException;
import static ch.cern.eam.wshub.core.tools.DataTypeTools.isEmpty;
import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RequestScoped
public class AuthenticationTools {

    @Inject
    private HttpServletRequest request;
    @Inject
    private InforClient inforClient;
    @Inject
    private ApplicationData applicationData;
    @Inject
    private OpenIdTools openIdTools;

    public InforContext getInforContext() throws InforException
    {
        String user = null;
        String password = null;
        String organization = null;
        String tenant = null;
        String sessionid = null;
        String authenticationMode = applicationData.getAuthenticationMode();

        if ("LOCAL".equalsIgnoreCase(authenticationMode)) {
            user = applicationData.getDefaultUser();
            if (user == null) {
                user = System.getProperty("DEFAULT_USER").toUpperCase();
            }
            user = getUnderlyingUser(authenticationMode, user);
            password = applicationData.getAdminPassword();
            tenant = applicationData.getTenant();
            organization = applicationData.getDefaultOrganization();
        } else if ("SSO".equalsIgnoreCase(authenticationMode)) {
            user = getUnderlyingUser(authenticationMode, request.getHeader("ADFS_LOGIN").toUpperCase());
            password = applicationData.getAdminPassword();
            tenant = applicationData.getTenant();
            organization = applicationData.getDefaultOrganization();
        } else if ("OPENID".equalsIgnoreCase(authenticationMode)) {
            String header =  request.getHeader("Authorization");
            user = getUnderlyingUser(authenticationMode, openIdTools.getUserName(header));
            password = applicationData.getAdminPassword();
            tenant = applicationData.getTenant();
            organization = applicationData.getDefaultOrganization();
        } else {
            user = request.getHeader("INFOR_USER");
            password = request.getHeader("INFOR_PASSWORD");
            tenant = request.getHeader("INFOR_TENANT");
            organization = request.getHeader("INFOR_ORGANIZATION");
            sessionid = request.getHeader("INFOR_SESSIONID");
        }

        InforContext inforContext = new InforContext();

        // Organization
        if (isEmpty(organization)) {
            throw inforClient.getTools().generateFault("Organization is required.");
        }
        inforContext.setOrganizationCode(organization);

        // Tenant
        if (isEmpty(tenant)) {
            throw inforClient.getTools().generateFault("Tenant is required.");
        }
        inforContext.setTenant(tenant);

        // Credentials, Session ID
        if (isNotEmpty(user) && isNotEmpty(password)) {
            Credentials credentials = new Credentials();
            credentials.setUsername(user);
            credentials.setPassword(password);
            inforContext.setCredentials(credentials);
        } else if (isNotEmpty(sessionid)) {
            inforContext.setSessionID(sessionid);
        } else {
            throw inforClient.getTools().generateFault("Credentials or Session ID is required.");
        }

        return inforContext;
    }

    public String getUnderlyingUser(String authenticationMode, String authenticatedUser) {
        boolean allowImpersonation = Arrays.asList("SSO", "LOCAL", "OPENID").contains(authenticationMode)
                && applicationData.getServiceAccount().equals(authenticatedUser)
                && isNotEmpty(request.getHeader("INFOR_USER"));
        return allowImpersonation ? request.getHeader("INFOR_USER") : authenticatedUser;
    }

    public InforContext getR5InforContext() throws InforException {
        InforContext inforContext = this.getInforContext();

        // Username
        if (isEmpty(applicationData.getAdminUser())) {
            inforContext.getCredentials().setUsername(request.getHeader("INFOR_USER"));
        } else {
            inforContext.getCredentials().setUsername(applicationData.getAdminUser());
        }

        // Password
        if (isEmpty(applicationData.getAdminPassword())) {
            inforContext.getCredentials().setPassword(request.getHeader("INFOR_PASSWORD"));
        } else {
            inforContext.getCredentials().setPassword(applicationData.getAdminPassword());
        }

        return inforContext;
    }

    public String getOrganizationCode() {
        try {
            return inforClient.getTools().getOrganizationCode(getInforContext());
        } catch (Exception exception) {
            return null;
        }
    }


}
